package org.nuxeo;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import com.yammer.metrics.reporting.ConsoleReporter;

/**
 * Test jdbc connection and network latency
 *
 */
public class App {

    private static final Log log = LogFactory.getLog(App.class);

    private final static Timer connTimer = Metrics.defaultRegistry().newTimer(
            App.class, "connection", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);

    private final static Timer execTimer = Metrics.defaultRegistry().newTimer(
            App.class, "execution", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);

    private final static Timer fetchingTimer = Metrics.defaultRegistry().newTimer(
            App.class, "fetching", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);

    private static final String CONFIG_KEY = "config";

    private static final String DEFAULT_CONFIG_FILE = "jdbctester.properties";

    private static final String REPEAT_KEY = "repeat";

    private static final String DEFAULT_REPEAT = "100";

    public static void main(String[] args) throws SQLException, IOException {

        Properties prop = readProperties();
        String user = prop.getProperty("user");
        String password = prop.getProperty("password");
        String connectionURL = prop.getProperty("url");
        String driver = prop.getProperty("driver");
        String query = prop.getProperty("query");

        log.info("Connect to:" + connectionURL + " from " + getHostName());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        final ConsoleReporter reporter = new ConsoleReporter(printStream);

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TimerContext tc = null;
        int repeat = Integer.valueOf(System.getProperty(REPEAT_KEY,
                DEFAULT_REPEAT)).intValue();

        log.info("Submiting " + repeat + " queries: " + query);
        try {
            Class.forName(driver);
            tc = connTimer.time();
            conn = DriverManager.getConnection(connectionURL, user, password);
            tc.stop();
            ps = conn.prepareStatement(query,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            int paramCount = countOccurrences(query, '?');
            for (int i = 1; i <= paramCount; i++) {
                String key = "p" + i;
                String param = prop.getProperty(key);
                if (param == null) {
                    break;
                }
                log.info(key + " = " + param);
                String type = "object";
                if (param.contains(":")) {
                    type = param.split(":", 2)[0];
                    param = param.split(":", 2)[1];
                }
                if (type.equalsIgnoreCase("object")) {
                    ps.setObject(i, (Object) param);
                } else if (type.equalsIgnoreCase("string")) {
                    ps.setString(i, param);
                } else if (type.equalsIgnoreCase("nstring")) {
                    ps.setNString(i, param);
                } else {
                    log.warn("Unknown type " + type + " use setObject");
                    ps.setObject(i, (Object) param);
                }
            }

            int rows = 0;
            int bytes = 0;

            for (int i = 0; i < repeat; i++) {
                tc = execTimer.time();
                rs = ps.executeQuery();
                tc.stop();
                tc = fetchingTimer.time();
                ResultSetMetaData rsmd = rs.getMetaData();
                int cols = rsmd.getColumnCount();
                while (rs.next()) {
                    rows++;
                    for (int c = 1; c <= cols; c++) {
                        bytes += rs.getBytes(1).length;
                    }
                }
                rs.close();
                tc.stop();
                // don't stress too much
                Thread.sleep((int) (Math.random() * 100));
            }
            log.info("Fetched rows: " + rows + ", total bytes: " + bytes
                    + ", bytes/rows: " + ((float) bytes) / rows);

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        reporter.run();
        try {
            String content = baos.toString("ISO-8859-1");
            log.info(content);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }

    }

    private static String getHostName() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "unknown";
        }
        return hostname;
    }

    private static int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }

    private static Properties readProperties() throws IOException {
        Properties prop = new Properties();
        FileInputStream fs;
        try {
            fs = new FileInputStream(System.getProperty(CONFIG_KEY));
        } catch (FileNotFoundException e) {
            log.error("Property file not found: " + System.getProperty(CONFIG_KEY, CONFIG_KEY), e);
            return null;
        }
        try {
            prop.load(fs);
            fs.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("File not found " + DEFAULT_CONFIG_FILE, e);
        }
        return prop;
    }
}
