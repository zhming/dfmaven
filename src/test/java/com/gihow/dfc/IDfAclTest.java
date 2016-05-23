package com.gihow.dfc;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfUser;
import com.gihow.service.IDfAclService;
import com.gihow.service.IDfUserService;
import com.gihow.service.impl.DfAclServiceImpl;
import com.gihow.service.impl.DfUserServiceImpl;
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
        IDfAclService aclService = new DfAclServiceImpl();
        try {
            IDfUser user = userService.getUserByUsername("Administrator");
            String aclName = user.getACLName();
            assertEquals("dm_4501e25680000101", aclName);
            String aclDomain = user.getACLDomain();
            IDfACL dfACL = aclService.getAclByName(aclName);
            System.out.println("dfAcl_NAME: " + dfACL.getObjectName());
            System.out.println("dfAcl_PERMISSIONS: " + dfACL.getPermissions());
            System.out.println("dfAcl_ID: " + dfACL.getObjectId().getId());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
