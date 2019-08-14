
@echo off

:获取管理员权限

%1 mshta vbscript:CreateObject("Shell.Application").ShellExecute("cmd.exe","/c %~s0 ::","","runas",1)(window.close)&&exit

regsvr32 dm.dll /s "%~dp0"