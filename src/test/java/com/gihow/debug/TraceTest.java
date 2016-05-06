package com.gihow.debug;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-3
 * Time: 下午3:04
 * To change this template use File | Settings | File Templates.
 */
public class TraceTest {

    @Test
    public void pringlnTest(){
        Object obj = new String("aaa");
        String msg = "Hello DfDebug";
        Trace.println(obj, msg);
    }
}
