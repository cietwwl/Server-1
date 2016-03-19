#!/bin/sh
cd proto
mono ../Mac/protogen.exe -p:detectMissing -i:"$1:() -o:../GenCS/${protofile}.cs $1"${fnm:(-4)"
cd ..
