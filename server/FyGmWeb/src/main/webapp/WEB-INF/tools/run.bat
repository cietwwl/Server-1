@echo off
cd /d %~dp0
setlocal ENABLEDELAYEDEXPANSION
set extfile=.xlsx .xls
set pwd=%~dp0 
for /R "%pwd%" %%i in (*) do ( 
set filename=%%~nxi 
call :check %%~xi 
) 
exit 
 
:check 
for %%i in (%extfile%) do ( 
if "%1"=="%%i" echo start to convert !filename! && python convert.win.py !filename!
) 
goto :eof 
exit