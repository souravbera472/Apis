@echo off
set dir="%cd%"
set dir=%dir:"=%
@echo Starting Mongodb server...
bin\MongoDB\Server\5.0\bin\mongod --config %DIR%\bin\MongoDB\Server\5.0\bin\mongod.cfg