
export JAVA_HOME=/home/riss/apps/jdk1.8.0_261

mvn clean
mvn source:jar install -f ../docking-frames-core
mvn source:jar install -f ../docking-frames-common

VERSION=`mvn -f ../pom.xml org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v '\['`
echo "New version: $VERSION"
read
mvn -DallowSnapshots=true -DparentVersion=[$VERSION] versions:update-parent
mvn versions:set -DnewVersion=${VERSION} -f pom

mvn clean install
mvn install -f pom

mvn versions:commit
mvn versions:commit -f pom