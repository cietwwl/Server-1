set csPath=E:\proto\cs\
set javaPath=E:\proto\java\

@echo -----begin to explain shape file-----

..\java\protoc --java_out=%javaPath% %1
..\cs\protogen -p:detectMissing -i:%1 -o:%csPath%% %%~n1.cs

@echo -------------end----------------