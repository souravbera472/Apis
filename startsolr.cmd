@echo off
@IF EXIST "workbench.properties" (
@For /F "tokens=1,2 delims==" %%G IN (workbench.properties) DO (
@IF %%G==USE_SOLR  IF %%H==true (
@echo Starting Solr .....
@bin\solr\bin\solr.cmd start -c
) ELSE (
@echo off
@echo Solr not configured.
))
)

rem
rem echo solr started successfully.