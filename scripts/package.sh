#!/bin/sh
HERE=$(dirname $(readlink -e $0))
cd $HERE/..
mvn -o clean package || exit -1
mvn -o dependency:copy-dependencies || exit -1
VERSION=`ls target/jdbctester*.jar | sed -e "s/^[^0-9]*//" | sed -e "s/.jar$//"`
echo "### Packaging jdbctester $VERSION"
DIST=./target/jdbctester
rm -rf $DIST
mkdir -p $DIST || exit -1
mkdir -p $DIST/lib || exit -1
cp target/jdbctester-*.jar $DIST/lib || exit -1
cp target/dependency/*.jar $DIST/lib || exit -1
# Prevent shipping Oracle driver
rm $DIST/lib/ojdbc6*.jar
cp scripts/jdbctester.sh $DIST || exit -1
cp src/test/resources/oracle.properties $DIST || exit -1
cp src/test/resources/postgresql.properties $DIST || exit -1
cp README.md $DIST
chmod +x $DIST/jdbctester.sh || exit -1
cd $DIST; cd ..
tar czvf jdbctester-$VERSION.tgz jdbctester || exit -1
echo "### Done: `readlink -e jdbctester-$VERSION.tgz`"

