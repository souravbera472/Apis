package com.myproject.logger;

public interface LoggerInterface {

    void info(Object o);

    void error(Object o);

    void error(Object o,Throwable e);

    void warn(Object o);

}
