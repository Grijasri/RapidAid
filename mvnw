#!/bin/sh
# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup script for Unix/Linux/macOS
# ----------------------------------------------------------------------------

set -e

PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

SAVED="`pwd`"
CDPATH=""
cd "`dirname "$PRG"`/" >/dev/null
MAVEN_PROJECTBASEDIR="`pwd`"
cd "$SAVED" >/dev/null

WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
    WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
    echo "Downloading Maven Wrapper JAR..."
    if command -v curl >/dev/null 2>&1; then
        curl -s -L "$WRAPPER_URL" -o "$WRAPPER_JAR"
    elif command -v wget >/dev/null 2>&1; then
        wget -q "$WRAPPER_URL" -O "$WRAPPER_JAR"
    fi
fi

exec java -classpath "$WRAPPER_JAR" "-Dmaven.home=$MAVEN_PROJECTBASEDIR/.mvn/wrapper" "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" org.apache.maven.wrapper.MavenWrapperMain "$@"
