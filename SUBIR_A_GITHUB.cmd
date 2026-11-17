@echo off
cd /d "%~dp0"
where git >nul 2>nul
if errorlevel 1 (
 echo Instala Git para Windows desde https://git-scm.com/download/win y vuelve a ejecutar este archivo.
 pause
 exit /b 1
)
echo Crea un repositorio VACIO en GitHub llamado MetaPunto, sin README ni licencia.
set /p META_REPO=Pega la URL HTTPS del repositorio: 
if "%META_REPO%"=="" exit /b 1
git init -b main
if errorlevel 1 goto error
git add .
git -c user.name="MetaPunto" -c user.email="metapunto@users.noreply.github.com" commit -m "Primera version MetaPunto"
if errorlevel 1 goto error
git remote add origin "%META_REPO%"
if errorlevel 1 goto error
git push -u origin main
if errorlevel 1 goto error
echo Abre tu repositorio en GitHub, Actions, Compilar APK. Al finalizar descarga MetaPunto-APK.
pause
exit /b 0
:error
echo No se completo la subida. Copia el mensaje de error para revisarlo.
pause
exit /b 1
