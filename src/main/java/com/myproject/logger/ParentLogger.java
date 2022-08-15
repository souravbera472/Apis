package com.myproject.logger;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParentLogger implements LoggerInterface {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    private String className;
    private Logger myLogger = LogManager.getLogger(ParentLogger.class.getName());

    public ParentLogger(String name) {
        className = name;
    }

    @Override
    public void info(Object message) {
        Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.log(Level.INFO, (message == null) ? "null" : message.toString());
        }

    }

    @Override
    public void error(Object message) {
        Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.error((message == null) ? "null" : message.toString(), Level.ERROR);
        }
    }

    @Override
    public void error(Object message, Throwable object) {
        Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.error((message == null) ? "null" : message.toString(), Level.ERROR, object);
        }
    }

    @Override
    public void warn(Object message) {
        Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.warn((message == null) ? "null" : message.toString(), Level.WARN);
        }
    }

    private Logger getLogger() {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        if (elements.length >= 3 && elements[3].getClassName() != null) {
            return LogManager.getLogger(elements[3].getClassName());
        } else {
            return myLogger;
        }
    }
}
