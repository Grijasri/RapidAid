@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.2.0
@REM ----------------------------------------------------------------------------

@echo off
setlocal

set DIR_DEF=%~dp0
if "%DIR_DEF%" == "" set DIR_DEF=.\

set MAVEN_PROJECTBASEDIR=%DIR_DEF%

if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:init
set MAVEN_CMD_LINE_ARGS=%*

@REM Find the project base dir, i.e. the dir that contains the folder ".mvn".
@REM Fallback to current dir if not found.

set MAVEN_PROJECTBASEDIR=%CD%
:findBaseDir
if exist "%MAVEN_PROJECTBASEDIR%\.mvn" goto baseDirFound
set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR%\..
if not "%MAVEN_PROJECTBASEDIR%" == "" goto findBaseDir
set MAVEN_PROJECTBASEDIR=%CD%

:baseDirFound

set MAVEN_WRAPPER_SETTING_FILE="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"

for /f "tokens=1* delims==" %%A in (%MAVEN_WRAPPER_SETTING_FILE%) do (
    if "%%A"=="wrapperUrl" set WRAPPER_URL=%%B
    if "%%A"=="distributionUrl" set MAVEN_URL=%%B
)

if not "%WRAPPER_URL%"=="" goto downloadWrapper

set WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar

:downloadWrapper

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"

if exist %WRAPPER_JAR% goto run

echo Downloading %WRAPPER_URL% to %WRAPPER_JAR%
powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')"

:run
"%JAVA_HOME%\bin\java.exe" -classpath %WRAPPER_JAR% "-Dmaven.home=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CMD_LINE_ARGS%

if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
exit /B %ERROR_CODE%
