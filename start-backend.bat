@echo off
echo Compilando backend...
cd /d "c:\Users\Lab Ingenieria 12\Downloads\proyectoVeterinaria"

REM Usar Maven de IntelliJ
set MAVEN_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.2\plugins\maven\lib\maven3
set PATH=%MAVEN_HOME%\bin;%PATH%

call mvn clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Compilacion exitosa. Iniciando backend...
    echo ========================================
    echo.
    java -jar target\veterinaria-backend-1.0.0.jar
) else (
    echo.
    echo ========================================
    echo ERROR: La compilacion fallo
    echo ========================================
    pause
)
