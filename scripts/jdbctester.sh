#!/bin/sh
HERE=$(cd $(dirname $0); pwd -P)
if [ -x $1 ]; then
    echo "Error: Missing parameter"
    echo "Usage: $0 PROPERTY_FILE [REPEAT]"
    exit 1
fi
CONFIG=$(cd $(dirname $1); pwd)/$(basename $1)
if [ ! -r $1 ]; then
    echo "Error: property file $1, not found"
    exit 1
fi
shift;
REPEAT=${1:-10}
cd $HERE

java -Dconfig=$CONFIG -Drepeat=$REPEAT -Duser.language=en -Duser.country=US  -classpath $JAR:./lib/* org.nuxeo.App
