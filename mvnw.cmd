@ECHO OFF
SETLOCAL
WHERE mvn >NUL 2>&1
IF %ERRORLEVEL% EQU 0 (
  mvn %*
  EXIT /B %ERRORLEVEL%
) ELSE (
  ECHO Maven is required to run this project. Please install Apache Maven or update mvnw.cmd to point to a bundled distribution.
  EXIT /B 1
)
