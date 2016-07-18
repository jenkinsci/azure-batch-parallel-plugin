@ECHO OFF

SET jobId=%1
SET taskId=%2
SET storageAccount=%3
SET containerSasKey=%4
SET tempResultFolder=%5

SET tempZipFolder=tempZip%RANDOM%

copy task_stdout.txt %tempResultFolder%\%taskId% /Y 2>null
copy task_stderr.txt %tempResultFolder%\%taskId% /Y 2>null
mkdir %tempZipFolder%\logs

REM Step1: Zip %tempResultFolder% folder to %tempZipFolder%\logs\taskId.zip file, 
REM        so taskId\ folder will be in the zip file.

:step1
set step=1

pushd %tempResultFolder%
cmd /c python %AZ_BATCH_NODE_SHARED_DIR%\%AZ_BATCH_JOB_ID%\Zip.py zip %taskId% ..\%tempZipFolder%\logs\%taskId%.zip
popd

if %ERRORLEVEL% NEQ 0 goto retry

REM Step2: Upload %tempZipFolder% folder to storage, so logs/taskId.zip file will be in storage.
REM        SAS key may contain '%', should replace it with '%%' before passing to this .cmd script.

:step2
set step=2

blobxfer %storageAccount% %jobId% %tempZipFolder% --saskey %containerSasKey% --upload

if %ERRORLEVEL% EQU 0 goto eof  
if %ERRORLEVEL% NEQ 0 goto retry

:retry
goto :step%step%

:eof
EXIT  