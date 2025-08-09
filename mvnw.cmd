@ECHO OFF
setlocal
set DIR=%~dp0
if exist "%DIR%\.mvn\wrapper\maven-wrapper.jar" (
  set WRAPPER_JAR="%DIR%\.mvn\wrapper\maven-wrapper.jar"
) else (
  echo Wrapper jar not found. Please run 'mvn -N wrapper'.
  exit /b 1
)
java -Dmaven.multiModuleProjectDirectory=%CD% -cp %WRAPPER_JAR% org.apache.maven.wrapper.MavenWrapperMain %*
