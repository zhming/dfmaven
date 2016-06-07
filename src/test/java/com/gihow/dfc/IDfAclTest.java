package com.gihow.dfc;

import com.documentum.fc.client.*;
import com.documentum.fc.common.IDfLoginInfo;
import com.gihow.dfc.sessionmananger.ClientXUtils;
import com.gihow.dfc.sessionmananger.SessionManagerUtil;
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
    @Test
    public void getAclNameByUsernameTest(){
        IDfUserService userService = new DfUserServiceImpl();
        IDfAclService aclService = new DfAclServiceImpl();
        try {
            IDfUser user = userService.getUserByUsername("test1");
            String aclName = user.getACLName();
            assertEquals("dm_4501e25680001d00", aclName);
            String aclDomain = user.getACLDomain();
            IDfACL dfACL = aclService.getAclByName(aclDomain, aclName);
            System.out.println("dfAcl_NAME: " + dfACL.getObjectName());
            System.out.println("dfAcl_PERMISSIONS: " + dfACL.getPermissions());
            System.out.println("dfAcl_ID: " + dfACL.getObjectId().getId());
            System.out.println("dfAcl_domain: " + dfACL.getDomain());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail();
        }

    }

    @Test
    public void getCabinetByDqlTest(){

        IDfSessionManager sessionManager = null;
        IDfSession session = null;
        try {
            String dql = "select * from dm_sysobject(all)" +
                    "where acl_name ='dm_4501e25680001d00' and acl_domain = 'Administrator'" +
                    " and r_object_type='dm_cabinet'";
            sessionManager = ClientXUtils.getSessionManager();
            IDfLoginInfo loginInfo = ClientXUtils.getLoginInfo("test1", "test");
            sessionManager.setIdentity(StaticValuesUtil.DOCBASE, loginInfo);
            session = sessionManager.getSession(StaticValuesUtil.DOCBASE);
            IDfQuery query = ClientXUtils.getQuery(dql);
            IDfCollection collection = query.execute(session, DfQuery.DF_READ_QUERY);
            while (collection.next()) {
                IDfTypedObject typedObject = collection.getTypedObject();
                log.debug("object_format | " + typedObject);
                log.debug("object_name | " + typedObject.getString("object_name"));
                log.debug("r_object_id | " + typedObject.getString("r_object_id"));
                log.debug("owner_name | " + typedObject.getString("owner_name"));
                log.debug("owner_permit | " + typedObject.getInt("owner_permit"));
                log.debug("group_permit | " + typedObject.getInt("group_permit"));
                log.debug("world_permit | " + typedObject.getInt("world_permit"));
                log.debug("acl_name | " + typedObject.getString("acl_name"));
                log.debug("r_object_type | " + typedObject.getString("r_object_type"));
                log.debug("r_creation_date | " + typedObject.getString("r_creation_date"));
                log.debug("r_modify_date | " + typedObject.getString("r_modify_date"));
            }
            collection.close();
            log.debug("sql | " + SessionManagerUtil.getSqlString(session));
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }finally {
            sessionManager.release(session);
        }

    }

    @Test
    public void getDocumentsByDqlTest(){

        IDfSessionManager sessionManager = null;
        IDfSession session = null;
        try {
            String dql = "select * from dm_document where folder ('/Alibaba', descend)";
            sessionManager = ClientXUtils.getSessionManager();
            IDfLoginInfo loginInfo = ClientXUtils.getLoginInfo("Administrator", "gihow1qaz");
            sessionManager.setIdentity(StaticValuesUtil.DOCBASE, loginInfo);
            session = sessionManager.getSession(StaticValuesUtil.DOCBASE);
            IDfQuery query = ClientXUtils.getQuery(dql);
            IDfCollection collection = query.execute(session, DfQuery.DF_READ_QUERY);
            while (collection.next()) {
                IDfTypedObject typedObject = collection.getTypedObject();
                log.debug("object_format | " + typedObject);
                log.debug("object_name | " + typedObject.getString("object_name"));
                log.debug("r_object_id | " + typedObject.getString("r_object_id"));
                log.debug("owner_name | " + typedObject.getString("owner_name"));
                log.debug("owner_permit | " + typedObject.getInt("owner_permit"));
                log.debug("group_permit | " + typedObject.getInt("group_permit"));
                log.debug("world_permit | " + typedObject.getInt("world_permit"));
                log.debug("acl_name | " + typedObject.getString("acl_name"));
                log.debug("r_object_type | " + typedObject.getString("r_object_type"));
                log.debug("r_creation_date | " + typedObject.getString("r_creation_date"));
                log.debug("r_modify_date | " + typedObject.getString("r_modify_date"));
            }
            collection.close();
            log.debug("sql | " + SessionManagerUtil.getSqlString(session));
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }finally {
            sessionManager.release(session);
        }

    }


}
