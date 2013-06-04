#!/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-6-sun/
echo "Java Home is $JAVA_HOME"

export CLASSPATH=:./itf2brw.jar:$CLASSPATH
echo "CLASSPATH is $CLASSPATH"

$JAVA_HOME/bin/java -Xms128m -Xmx2048m org.catais.brw.App ./itf2brw_lv03.properties


