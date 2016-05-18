package com.gihow.dfc;

import com.gihow.security.session.Auth;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-18
 * Time: 下午3:26
 * To change this template use File | Settings | File Templates.
 */
public class AuthTest {
    @Test
    public void authDfcTest(){
        String username = "Administrator";
        String password = "gihow1qaz";
        assertEquals(true,Auth.authDfc(username, password));


    }
}
