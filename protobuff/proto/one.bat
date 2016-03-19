set csPath=E:\RXFS\Assets\Script\ProtobufClass\
set javaPath=E:\Server\FSApp\src\main\java\

@echo -----begin to explain shape file-----

..\java\protoc --java_out=%javaPath% %1
..\cs\protogen -p:detectMissing -i:%1 -o:%csPath%% %%~n1.cs

@echo -------------end----------------