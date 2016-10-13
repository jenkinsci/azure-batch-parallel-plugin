#!/bin/bash

InstallPip () {
  wget https://bootstrap.pypa.io/get-pip.py
  python get-pip.py
}

InstallBlobxfer () {
  platform=`python -mplatform`
  case "$platform" in 
    *suse*)
      zypper refresh
      zypper --non-interactive install gcc libffi-devel python-devel openssl-devel

      python -m pip install --upgrade pip
      pip install --upgrade blobxfer
      ;;
    *Ubuntu*)
      apt-get update
      apt-get --assume-yes install -y build-essential libssl-dev libffi-dev python-dev
      pip install --upgrade blobxfer
      ;; 
     *)  
        echo "Not supported on this OS yet" 2>&1
        exit 0 # Mark exit code 0 so there's chance to run customer's setup script
        ;; 
  esac
}

if ! [ -x "$(command -v pip)" ]; then
  echo 'pip is not installed, installing'
  InstallPip
fi

if ! [ -x "$(command -v blobxfer)" ]; then
  echo 'blobxfer is not installed, installing'  
  InstallBlobxfer
fi
