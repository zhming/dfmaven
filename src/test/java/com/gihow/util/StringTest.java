package com.gihow.util;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-6-7
 * Time: 下午4:44
 * To change this template use File | Settings | File Templates.
 */
public class StringTest {
    private Logger log = Logger.getLogger(StringTest.class);

    @Test
    public void stringTest(){
        log.debug(String.format("Hello, %s", "world!"));
        log.debug(String.format("Hello, %s, %s", "world!", "welcome"));
        log.debug(String.format("Hello, %s, %s, %s", "world!", "welcome", "ok"));
    }
}
