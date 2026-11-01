@echo off
REM ===================================================================
REM  Compiles the Unlucky Events mod into a jar.
REM  Just double-click this file. Requires JDK 25 or newer:
REM    https://learn.microsoft.com/java/openjdk/download
REM ===================================================================
setlocal

echo Checking for Java...
where java >nul 2>nul
if errorlevel 1 (
    echo.
    echo ERROR: Java was not found on your PATH.
    echo Install JDK 25 or newer, then run this again:
    echo   https://learn.microsoft.com/java/openjdk/download
    echo.
    pause
    exit /b 1
)

echo Building the mod ^(the first run downloads the toolchain and may take a minute^)...
call "%~dp0gradlew.bat" build
if errorlevel 1 (
    echo.
    echo Build FAILED - see the messages above.
    pause
    exit /b 1
)

echo.
echo Build succeeded! Your jar is in:
echo   %~dp0build\libs
echo.
echo Drop the jar (not the -sources one) into your server's mods folder.
echo.
pause
