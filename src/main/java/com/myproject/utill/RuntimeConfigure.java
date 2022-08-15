package com.myproject.utill;

import com.myproject.logger.KLogger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class RuntimeConfigure {
    public static void main(String[] args) {
        String hostname;
        try{
            hostname = InetAddress.getLocalHost().getHostAddress();
            KLogger.info(hostname);
            FileInputStream in = new FileInputStream("workbench.properties");
            Properties props = new Properties();
            props.load(in);
            in.close();

            FileOutputStream out = new FileOutputStream("workbench.properties");
            props.setProperty("SERVER_IP",hostname);
            props.store(out, null);
            out.close();
        }catch (UnknownHostException e){
            KLogger.error(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
