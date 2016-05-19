package com.gihow.dfc;

import com.documentum.fc.client.IDfUser;
import com.gihow.dfc.service.IDfUserService;
import com.gihow.dfc.service.impl.DfUserServiceImpl;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-19
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */
public class IDfAclTest {

    @Test
    public void getAclNameByUsernameTest(){
        IDfUserService userService = new DfUserServiceImpl();
        try {
            IDfUser user = userService.getUserByUsername("Administrator");
            String acl = user.getACLName();
            assertEquals("dm_4501e25680000101", acl);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
