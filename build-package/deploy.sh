
export JAVA_HOME=/home/riss/apps/jdk1.8.0_261

mvn -DpomFile=pom/pom.xml -Dsources=target/docking-frames-sources.jar -Dfile=target/docking-frames.jar -DrepositoryId=main-dev -Durl="ARCHIVA_URL" deploy:deploy-file