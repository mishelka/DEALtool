@echo off
:: -------------------------------------------------------------------------
:: start.bat    
:: Script for starting @doc.name@ Version @doc.version@ under Windows
:: Set either JAVA_HOME to point at your Java Development Kit installation.
:: or PATH to point at the java command
::
set _PROG_DIR=%~dp0
echo PROG_DIR %_PROG_DIR%
set RAPLA_JAVA=%JAVA_HOME%\bin\
if not "%JAVA_HOME%" == "" goto gotJavaHome
   set RAPLA_JAVA=""
:gotJavaHome
set RAPLA_JAVA_OPTIONS=%JAVA_OPTIONS%
if not "%JAVA_OPTIONS%" == "" goto gotJavaOptions
   set RAPLA_JAVA_OPTIONS="-Xmx128M"
:gotJavaOptions
cd %_PROG_DIR%
"%RAPLA_JAVA%java" %RAPLA_JAVA_OPTIONS% -jar raplabootstrap.jar %1 %2 %3 %4

