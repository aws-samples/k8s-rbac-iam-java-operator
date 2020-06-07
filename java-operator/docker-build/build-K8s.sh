#!/bin/bash
cd .. && mvn clean && mvn package && cd docker-build

JAR_SOURCE="../target/k8sJava.jar"
JAR_TARGET="k8sJava.jar"

/bin/rm -f $JAR_TARGET
/bin/cp $JAR_SOURCE $JAR_TARGET

NAME=k8s-java-controller
TAG=latest

SOURCE_IMG=$NAME:$TAG
/usr/local/bin/docker build -t $SOURCE_IMG .

SHA256=$(/usr/local/bin/docker inspect --format='{{index .Id}}' $SOURCE_IMG)
IFS=':' read -ra TOKENS <<< "$SHA256"

TARGET_IMG=123456789012.dkr.ecr.us-east-1.amazonaws.com/$NAME:${TOKENS[1]}
/usr/local/bin/docker tag $SOURCE_IMG $TARGET_IMG
/usr/local/bin/docker push $TARGET_IMG
