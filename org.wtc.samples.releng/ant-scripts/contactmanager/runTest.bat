@echo on
REM **************************************
REM This batch file launches an Ant script to
REM download, install and execute the WindowTester Example
REM **************************************
call ..\shared\ant\bin\ant.bat -f test.xml
pause
