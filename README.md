# JDBC Tester

This is a simple tool to get some number on network latency.  

Also it can be used to test prepared statement from JDBC.


# Install

Get the latest package from: 
<https://github.com/bdelbosc/jdbctester/downloads>
 
Then install:

       tar xzvf jdbctester-*.tgz

# Configuration

Create a properties file that will contain the database access and the
SQL query.

You can find 2 examples of properties file in the package
oracle.properties and postgresql.properties.

You can use a simple round trip sql command or a complex prepared
statement with parameters.

The default jdbctester.sh set the following option you shoud check
that it matches your database NLS.

     -Duser.language=en -Duser.country=US 
     

# Usage

Invocation:

       /path/to/jdbctester.sh PROPERTY_FILE [REPEAT]
    
REPEAT is number of time to repeat the SQL query

Example:

      ./jdbctester.sh postgresql.property 10

This will create a jdbctester.log file that looks like this:

        Connect to:jdbc:postgresql://localhost:5432/bdtest2 from strix
        Submiting 10 queries: SELECT 1;
        Fetched rows: 10, total bytes: 25, bytes/rows: 2.5

        connection:
                   count = 1
               mean rate = 0.44 calls/s
           1-minute rate = 0.00 calls/s
           5-minute rate = 0.00 calls/s
          15-minute rate = 0.00 calls/s
                     min = 946.97ms
                     max = 946.97ms
                    mean = 946.97ms
                  stddev = 0.00ms
                  median = 946.97ms
                    75% <= 946.97ms
                    95% <= 946.97ms
                    98% <= 946.97ms
                    99% <= 946.97ms
                  99.9% <= 946.97ms
      
        execution:
                   count = 10
               mean rate = 4.39 calls/s
           1-minute rate = 0.00 calls/s
           5-minute rate = 0.00 calls/s
          15-minute rate = 0.00 calls/s
                     min = 35.60ms
                     max = 101.62ms
                    mean = 44.68ms
                  stddev = 20.06ms
                  median = 38.74ms
                    75% <= 40.07ms
                    95% <= 101.62ms
                    98% <= 101.62ms
                    99% <= 101.62ms
                  99.9% <= 101.62ms
      
        fetching:
                   count = 10
               mean rate = 4.39 calls/s
           1-minute rate = 0.00 calls/s
           5-minute rate = 0.00 calls/s
          15-minute rate = 0.00 calls/s
                     min = 0.15ms
                     max = 17.35ms
                    mean = 1.87ms
                  stddev = 5.44ms
                  median = 0.15ms
                    75% <= 0.16ms
                    95% <= 17.35ms
                    98% <= 17.35ms
                    99% <= 17.35ms
                  99.9% <= 17.35ms
      


# About Nuxeo

Nuxeo provides a modular, extensible Java-based
[open source software platform for enterprise content management](http://www.nuxeo.com/en/products/ep)
and packaged applications for
[document management](http://www.nuxeo.com/en/products/document-management),
[digital asset management](http://www.nuxeo.com/en/products/dam) and
[case management](http://www.nuxeo.com/en/products/case-management). Designed
by developers for developers, the Nuxeo platform offers a modern
architecture, a powerful plug-in model and extensive packaging
capabilities for building content applications.

More information on: <http://www.nuxeo.com/>
