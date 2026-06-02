@echo off
:: Carga las variables del .env y arranca la aplicación
for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
    if not "%%A"=="" if not "%%A:~0,1%"=="#" set "%%A=%%B"
)
mvnw.cmd spring-boot:run %*
