#!/bin/bash

jobId=$1
taskId=$2
storageAccount=$3
containerSasKey=$4
tempResultFolder=$5

tempZipFolder=tempZip$RANDOM

cp /f task_stdout.txt $tempResultFolder/$taskId 2>null
cp /f task_stderr.txt $tempResultFolder/$taskId 2>null
mkdir -p $tempZipFolder/logs

cd $tempResultFolder
python $AZ_BATCH_NODE_SHARED_DIR/$AZ_BATCH_JOB_ID/Zip.py zip $taskId ../$tempZipFolder/logs/$taskId.zip
if [ $? -ne 0 ]
then
  exit $?
fi
cd ..

# Step2: Upload $tempZipFolder folder to storage, so logs/taskId.zip file will be in storage.

blobxfer $storageAccount $jobId $tempZipFolder --saskey $containerSasKey --upload
