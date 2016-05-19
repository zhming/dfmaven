package com.gihow.debug;

import com.gihow.util.Base64;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-13
 * Time: 下午4:00
 * To change this template use File | Settings | File Templates.
 */
public class Base64Test {
    @Test
    public void descTest(){
        String ss = "gihow1qaz";
            System.out.println("aaaaaaaaaaaaaaaaa : "  + Base64.encode(ss));
    }
}
