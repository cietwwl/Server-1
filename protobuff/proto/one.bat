set csPath=E:\git-fs\server\protobuff\proto\cs\
set javaPath=E:\git-fs\server\protobuff\proto\java\

@echo -----begin to explain shape file-----

..\java\protoc --java_out=%javaPath% %1
..\cs\protogen -p:detectMissing -i:%1 -o:%csPath%% %%~n1.cs

@echo -------------end----------------