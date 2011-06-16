#!/bin/bash

# this will install build in your local maven repo ${user.home}/.m2/repository/

cd ../

mvn   clean   source:jar   javadoc:jar   install     --define skipTests

