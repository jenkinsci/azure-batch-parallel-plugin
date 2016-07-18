import os
import sys;
import zipfile

if sys.argv[1].lower() == "unzip":
    zipFile= sys.argv[2]; 
    destFolder= sys.argv[3];

    z=zipfile.ZipFile(zipFile); 
    z.extractall(destFolder);
else:
    srcFolder= sys.argv[2];
    destZipFile= sys.argv[3]; 

    zipf = zipfile.ZipFile(destZipFile, 'w', zipfile.ZIP_DEFLATED)
    for root, dirs, files in os.walk(srcFolder):
        for file in files:
            zipf.write(os.path.join(root, file))
    zipf.close()