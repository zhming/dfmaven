package com.gihow.dfc;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.client.relationship.IDfQualificationIdentity;
import com.documentum.fc.common.IDfLoginInfo;
import com.gihow.dfc.sessionmananger.ClientXUtils;
import com.gihow.dfc.sessionmananger.SessionManagerUtil;
import com.gihow.util.StaticValuesUtil;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-24
 * Time: 下午4:40
 * To change this template use File | Settings | File Templates.
 */
public class DqlTest{
    @Test
    public void myDqlTest(){
        IDfSessionManager sessionManager = null;
        IDfSession session = null;
        try {
            sessionManager = ClientXUtils.getSessionManager();
            IDfLoginInfo loginInfo = ClientXUtils.getLoginInfo("Administrator", "gihow1qaz");
            sessionManager.setIdentity(StaticValuesUtil.DOCBASE, loginInfo);
            session = sessionManager.getSession(StaticValuesUtil.DOCBASE);
            String dql = "dm_acl where r_object_id='4501e25680000101'";
            IDfACL acl = (IDfACL)session.getObjectByQualification(dql);
            assertEquals("4501e25680000101", acl.getObjectId().getId());
//            dql = "dm_acl where r_object_name='Administrator'";
//            acl = (IDfACL)session.getObjectByQualification(dql);
//            assertEquals("4501e25680000101", acl.getObjectId().getId());
            dql = " dm_user where user_name='Administrator'";
            IDfUser user = (IDfUser)session.getObjectByQualification(dql);
            assertEquals("4501e25680000101",user.getUserName());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }finally {
            if (session != null){
                sessionManager.release(session);
            }

        }
    }
}
