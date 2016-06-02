package com.gihow.dfc;

import com.documentum.fc.client.*;
import com.documentum.fc.common.IDfLoginInfo;
import com.gihow.dfc.sessionmananger.ClientXUtils;
import com.gihow.service.IDfAclService;
import com.gihow.service.IDfUserService;
import com.gihow.service.impl.DfAclServiceImpl;
import com.gihow.service.impl.DfUserServiceImpl;
import com.gihow.util.StaticValuesUtil;
import org.apache.log4j.Logger;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-19
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */
public class IDfAclTest {
    private Logger log = Logger.getLogger(IDfAclTest.class);
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
            System.out.println("dfAcl_domain: " + dfACL.getDomain());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Test
    public void getCabinetByDqlTest(){

        IDfSessionManager sessionManager = null;
        IDfSession session = null;
        try {
            String dql = "select * from dm_sysobject(all)" +
                    "where acl_name ='dm_4501e25680000101' and acl_domain = 'Administrator'" +
                    " and r_object_type='dm_folder'";
            sessionManager = ClientXUtils.getSessionManager();
            IDfLoginInfo loginInfo = ClientXUtils.getLoginInfo("Administrator", "gihow1qaz");
            sessionManager.setIdentity(StaticValuesUtil.DOCBASE, loginInfo);
            session = sessionManager.getSession(StaticValuesUtil.DOCBASE);
            IDfQuery query = ClientXUtils.getQuery(dql);
            IDfCollection collection = query.execute(session, DfQuery.DF_READ_QUERY);
            while (collection.next()) {
                IDfTypedObject typedObject = collection.getTypedObject();
                log.debug("object_format | " + typedObject);
            }
            collection.close();
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }

    }
}
