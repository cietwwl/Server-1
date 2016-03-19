#!/bin/sh
cd proto
mono ../Mac/protogen.exe  -p:detectMissing -i:$1 -o:../GenCS/$2
cd ..
