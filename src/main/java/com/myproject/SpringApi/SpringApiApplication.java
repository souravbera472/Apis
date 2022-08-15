package com.myproject.SpringApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;
import java.util.Properties;


@SpringBootApplication
public class SpringApiApplication {
    private static boolean server;
    private static boolean usePass;
    private static String pass;


    public static void main(String[] args) {
        //FileReader reader;
        try(FileReader reader = new FileReader("workbench.properties")) {
            Properties p = new Properties();
            p.load(reader);
            server = Boolean.parseBoolean(p.getProperty("TOMCAT_SERVER", "false"));
            usePass = Boolean.parseBoolean(p.getProperty("USE_PASSWORD", "false"));
            pass = p.getProperty("TOMCAT_PASSWORD");
            System.out.println("Server: " + server);
//            Properties q = new Properties();
//            //q.replace("server.port",)
//            q.setProperty("server.port","45450");
//            q.store(new FileWriter("src/main/resources/application.properties"),"Write in port from main");
            if (server && pass.equals("sourav@123")) {
                //Thread.sleep(5000);
                startServer(args);
            } else if (server && usePass) {
                startServer(args);
            } else {
                System.out.println("Tomcat server not configured");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private static void startServer(String[] args) {
        System.out.println("Staring Application server...");
        SpringApplication.run(SpringApiApplication.class, args);
        System.out.println("Application server started successfully");
    }

}
