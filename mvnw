#!/bin/sh
BASEDIR=$(dirname "$0")
WRAPPER_JAR="$BASEDIR/.mvn/wrapper/maven-wrapper.jar"
if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Wrapper jar not found. Please run 'mvn -N wrapper' to generate it." >&2
  exit 1
fi
exec java -Dmaven.multiModuleProjectDirectory="$PWD" -cp "$WRAPPER_JAR" org.apache.maven.wrapper.MavenWrapperMain "$@"
