%echo off

set JAVA_OPT=-Xms8m -Xmx64m -Xss256k -XX:+UseSerialGC -XX:TieredStopAtLevel=1 ^
    -XX:InitialCodeCacheSize=128k -XX:CICompilerCount=1  

java %JAVA_OPT% -jar "%~f0" %*

exit /b %ERRORLEVEL%
