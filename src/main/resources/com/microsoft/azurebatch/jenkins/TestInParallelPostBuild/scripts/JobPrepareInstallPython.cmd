@ECHO OFF

REM This script must be run under System Admin.

REM Download and install python

reg query HKCU\software\Python 2> null
if ERRORLEVEL 1 GOTO CHECK_PYTHON_HKLM
GOTO :PYTHON_INSTALLED

:CHECK_PYTHON_HKLM
reg query HKLM\software\Python 2> null
if ERRORLEVEL 1 GOTO NO_PYTHON
GOTO :PYTHON_INSTALLED

:NO_PYTHON

cmd /c powershell (New-Object Net.WebClient).DownloadFile('https://www.python.org/ftp/python/3.5.1/python-3.5.1-amd64.exe', 'python-3.5.1-amd64.exe')
cmd /c python-3.5.1-amd64.exe /passive PrependPath=1 Include_test=0 InstallAllUsers=1

REM Add python to Path

SETLOCAL ENABLEEXTENSIONS
set ORGPATH=%PATH%
for /f "tokens=2*" %%A in ('REG QUERY "HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v Path ^| FIND /I "Path"') DO (
SET path=%%B
)
SET PATH=%ORGPATH%;%PATH%

:PYTHON_INSTALLED

REM Install blobxfer

cmd /c python -m pip install --upgrade pip
cmd /c pip install blobxfer

