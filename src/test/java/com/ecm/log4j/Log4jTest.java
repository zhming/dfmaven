package com.ecm.log4j;

import org.apache.log4j.LogManager;
import org.junit.Test;


/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-19
 * Time: 下午3:39
 * To change this template use File | Settings | File Templates.
 */
public class Log4jTest {
    private org.apache.log4j.Logger logger = LogManager.getLogger(Log4jTest.class.getName());
    @Test
    public void myLog4jTest(){
        logger.debug("Hello, {} {}" + "Log4j2" + "world!");
    }

}
