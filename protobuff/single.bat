set javaPath=F:\server\server\FSApp\src\main\java\
cd proto
@echo -----begin to explain java file-----


..\java\protoc --java_out=%javaPath%  GroupCopyBattle.proto


@echo -------------end----------------
pause