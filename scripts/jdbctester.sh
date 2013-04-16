#!/bin/sh
CONFIG=`readlink -e $1`; shift
REPEAT=${1:-10}
HERE=$(cd $(dirname $0); pwd -P) 
cd $HERE
java -Dconfig=$CONFIG -Drepeat=$REPEAT -Duser.language=en -Duser.country=US  -classpath $JAR:./lib/* org.nuxeo.App
