set csPath=F:\NewGitSource\store\server\protobuff\cs\
set javaPath=F:\NewGitSource\store\server\protobuff\java\

@echo -----begin to explain shape file-----

..\java\protoc --java_out=%javaPath% %1
..\cs\protogen -p:detectMissing -i:%1 -o:%csPath%% %%~n1.cs

@echo -------------end----------------