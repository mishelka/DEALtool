@echo off
:: -------------------------------------------------------------------------
:: startserver.bat    
:: Script for starting @doc.name@ Version @doc.version@ server under Windows
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
   set RAPLA_JAVA_OPTIONS="-Xmx512M"
:gotJavaOptions

:: Backward compatibility for old versions
if not "%1" == "import" goto conti1
  call rapla.bat import
goto finish
:conti1
if not "%1" == "export" goto conti2
  call rapla.bat export
goto finish
:conti2

echo USING OPTIONS %RAPLA_JAVA_OPTIONS%
cd %_PROG_DIR% 
"%RAPLA_JAVA%java"  %RAPLA_JAVA_OPTIONS% -cp raplabootstrap.jar org.rapla.bootstrap.RaplaServerLoader %1 %2 %3 %4
:finish

