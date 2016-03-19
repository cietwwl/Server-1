@echo off
cd /d %~dp0
setlocal ENABLEDELAYEDEXPANSION
mvn install
exit