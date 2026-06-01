@rem Gradle wrapper for Windows
@if "%DEBUG%"=="" @echo off
setlocal
set GRADLE_VERSION=8.5
set GRADLE_DIR=%USERPROFILE%\.gradle\wrapper\dists\gradle-%GRADLE_VERSION%-bin
if not exist "%GRADLE_DIR%" (
    echo Downloading Gradle %GRADLE_VERSION%...
    mkdir "%GRADLE_DIR%"
    curl -sL "https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip" -o %%TEMP%%\gradle.zip
    powershell -command "Expand-Archive -Path '%%TEMP%%\gradle.zip' -DestinationPath '%GRADLE_DIR%'"
)
"%GRADLE_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat" %*
