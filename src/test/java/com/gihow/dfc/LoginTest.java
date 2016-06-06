package com.gihow.dfc;

import com.documentum.fc.client.IDfUser;
import com.gihow.security.session.Auth;
import com.gihow.service.IDfUserService;
import com.gihow.service.impl.DfUserServiceImpl;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-6-6
 * Time: 上午10:23
 * To change this template use File | Settings | File Templates.
 */
public class LoginTest {
    @Test
    public void mocoLoginTest(){
        String uid = "1101e25680001501";
        IDfUserService uservice = new DfUserServiceImpl();
        String aclId = "";
        try {
            IDfUser user = uservice.getUserById(uid);
            aclId = user.getACLName();

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
}
