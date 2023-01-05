@echo off
@echo Stopping MongoDb server....
taskkill /f /im mongod.exe
@echo MongoDb server stop successfully.