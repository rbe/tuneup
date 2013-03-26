@echo off

set ACTUAL=%~dp0
set BASE=%ACTUAL:~0,-4%
set APPDATA=%BASE%appdata
set LOCALAPPDATA=%APPDATA%\Local
set USERPROFILE=%BASE%userprofile
set TOR=%BASE%tor

mkdir %APPDATA%
mkdir %LOCALAPPDATA%
mkdir %USERPROFILE%

pushd
cd %TOR%
rem start App\vidalia
start App\tor
popd

set CLASSPATH=
set CP=%BASE%lib\*
java -cp "%CP%" tuneup.TuneUpApp

taskkill /im tor.exe
