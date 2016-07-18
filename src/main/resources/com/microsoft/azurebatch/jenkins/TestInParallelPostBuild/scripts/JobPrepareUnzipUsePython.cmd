@ECHO OFF

REM Add python to Path

SETLOCAL ENABLEEXTENSIONS
set ORGPATH=%PATH%
for /f "tokens=2*" %%A in ('REG QUERY "HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v Path ^| FIND /I "Path"') DO (
SET path=%%B
)
SET PATH=%ORGPATH%;%PATH%

REM Unzip the file
python Zip.py unzip %1 %2