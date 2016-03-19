#!/bin/sh
mkdir GenCS
cd proto

for protofile in '*.proto';
do
	mono ../Mac/protoGen.exe -p:detectMissing -i:${protofile} -o:../GenCS/${protofile}.cs
done;

cd ..