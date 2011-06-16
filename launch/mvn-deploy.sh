#!/bin/bash

# this will deploy build to the remote sonatype repo

cd ../

mvn   clean   source:jar   javadoc:jar   deploy     --define skipTests

