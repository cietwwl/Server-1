set rootpath=%cd%
echo -------show run path---------
echo %rootpath%

echo --------check file path--------
if not exist %rootpath%\cs\ (mkdir %rootpath%\cs) else (echo 'cs' exist)
if not exist %rootpath%\java\ (mkdir %rootpath%\java) else (echo 'java' exist)

echo -------set path--------
set csPath=%rootpath%\cs\
set javaPath=%rootpath%\java\

set /p a=
echo %a%

@echo -----begin to explain shape file-----

..\java\protoc --java_out=%javaPath% %a%
..\cs\protogen -p:detectMissing -i:%a% -o:%csPath%%a:proto=cs%

@echo -------------end----------------

pause