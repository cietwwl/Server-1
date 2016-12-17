set csPath=F:\NewGitSource\gameNotice\server\protobuff\cs\
set javaPath=F:\NewGitSource\gameNotice\server\protobuff\java\

@echo -----begin to explain shape file-----

..\java\protoc --java_out=%javaPath% %1
..\cs\protogen -p:detectMissing -i:%1 -o:%csPath%% %%~n1.cs

@echo -------------end----------------