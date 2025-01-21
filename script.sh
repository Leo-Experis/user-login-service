#!/bin/bash

# $1 = relative path to where .jar file is, e.g. build/libs

echo "Removing old .jar from $1"
rm -r $1

echo "Building project to .jar"
./gradlew build -x test

echo "Removing -plain.jar"
rm `find $1 -name '*plain.jar'`

jar_name=`find $1 -name '*.jar' -exec basename \{} \;`

echo "Creating docker image"
docker buildx build --build-arg JAR_PATH=$1 --build-arg JAR_NAME=$jar_name -t docker-hotel .

echo "Creating containers"
docker compose up -d

