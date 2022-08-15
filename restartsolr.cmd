@echo off
@IF EXIST "workbench.properties" (
@For /F "tokens=1,2 delims==" %%A IN (workbench.properties) DO (
    @IF %%A==SOLR_PORT (
    @echo restarting solr..
    bin\solr\bin\solr.cmd restart -c -p %%B
)
)
)
rem bin\solr\bin\solr.cmd restart -c -p 8983