package com.myproject.utill;

import com.myproject.logger.KLogger;

import java.io.FileReader;
import java.util.Properties;

public class StartMongoServer {
    public static void StartMongoDb(){
        try(FileReader reader = new FileReader("workbench.properties")){
            Properties p = new Properties();
            p.load(reader);
           String host = p.getProperty("SERVER_IP","127.0.0.1");
           String port = String.valueOf(Integer.parseInt(p.getProperty("MONGO_PORT", "6000")));
        }catch(Exception e){
            KLogger.error(e);
            throw new RuntimeException(e);
        }
    }

}
