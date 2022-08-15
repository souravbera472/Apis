package com.myproject.logger;


public class KLogger {
    //-Dlog4j2.configurationFile=file:/home/atul/log4j2.properties
    //public static Logger log;

//    static {
//
//        String fileName = "src/main/resources/log4j2.properties";
//        File config = new File(fileName);
//        System.setProperty(ConfigurationFactory.CONFIGURATION_FILE_PROPERTY, config.toURI().toString());
//        //log.debug("log debug");
//    }

    private final static LoggerInterface logger = new ParentLogger(KLogger.class.getName());


    public static void info(Object message) {
        logger.info(message);
    }

    public static void error(Object error) {
        logger.error(error);
    }

    public static void error(Object error, Throwable e) {
        logger.error(error, e);
    }

    public static void warn(Object warn) {
        logger.warn(warn);
    }
}
