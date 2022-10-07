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
    public static void main(String[] args) throws UnknownHostException {
        String hostname = InetAddress.getLocalHost().getHostAddress();
        writeOnProperties(hostname, "SERVER_IP", "workbench.properties");
        writePortForTomcat();
    }

    private static void writePortForTomcat() {
        String port = Utillity.getWorkbenchValue("TOMCAT_PORT", "45450");
        writeOnProperties(port, "server.port", "src/main/resources/application.properties");

    }

    private static void writeOnProperties(String value, String keyValue, String fileName) {
        try {
            KLogger.info(value);
            FileInputStream in = new FileInputStream(fileName);
            Properties props = new Properties();
            props.load(in);
            in.close();
            FileOutputStream out = new FileOutputStream(fileName);
            props.setProperty(keyValue, value);
            props.store(out, null);
            out.close();
        } catch (UnknownHostException e) {
            KLogger.error(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
