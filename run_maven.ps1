# PowerShell script to download and execute Maven automatically
$ErrorActionPreference = "Stop"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

$MavenVersion = "3.9.6"
$MavenDir = Join-Path $PSScriptRoot ".mvn\apache-maven-$MavenVersion"

$MvnPath = "$MavenDir\bin\mvn.cmd"
Write-Host "Running Maven with JAVA_HOME=$env:JAVA_HOME: $MvnPath $args" -ForegroundColor Green
& $MvnPath $args
