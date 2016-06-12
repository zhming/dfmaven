package com.ecm.dfc;

import com.documentum.fc.client.*;
import com.documentum.fc.common.IDfLoginInfo;
import com.ecm.dfc.sessionmananger.ClientXUtils;
import com.ecm.util.StaticValuesUtil;
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
public class DqlTest {
    @Test
    public void myDqlTest() {
        IDfSessionManager sessionManager = null;
        IDfSession session = null;
        try {
            sessionManager = ClientXUtils.getSessionManager();
            IDfLoginInfo loginInfo = ClientXUtils.getLoginInfo("Administrator", "gihow1qaz");
            sessionManager.setIdentity(StaticValuesUtil.DOCBASE, loginInfo);
            session = sessionManager.getSession(StaticValuesUtil.DOCBASE);
            String dql = "dm_acl where r_object_id='4501e25680000101'";
            IDfACL acl = (IDfACL) session.getObjectByQualification(dql);
            assertEquals("4501e25680000101", acl.getObjectId().getId());
//            dql = "dm_acl where r_object_name='Administrator'";
//            acl = (IDfACL)session.getObjectByQualification(dql);
//            assertEquals("4501e25680000101", acl.getObjectId().getId());
            dql = "select * from dm_folder(all) where folder('/BourneCabinet',descend)'";
            IDfQuery query = ClientXUtils.getQuery(dql);
            IDfCollection collection = query.execute(session, DfQuery.DF_READ_QUERY);
            while (collection.next()) {
//                for (int i = 0; i < collection.getAttrCount(); i++)
//                {
//                    IDfAttr attr = collection.getAttr(i);
//                    System.out.println("Attribute Name: " + attr.getName());
//
//                }
                IDfTypedObject typedObject = collection.getTypedObject();
                System.out.println("r_object_id: " + typedObject.getValue("r_object_id"));
                System.out.println("object_name: " + typedObject.getValue("object_name"));
                System.out.println("title: " + typedObject.getValue("title"));
                System.out.println("resolution_label: " + typedObject.getValue("resolution_label"));
                System.out.println("owner_name: " + typedObject.getValue("owner_name"));
                System.out.println("owner_permit: " + typedObject.getValue("owner_permit"));
                System.out.println("group_name: " + typedObject.getValue("group_name"));
                System.out.println("group_permit: " + typedObject.getValue("group_permit"));
                System.out.println("world_permit: " + typedObject.getValue("world_permit"));
                System.out.println("log_entry: " + typedObject.getValue("log_entry"));
                System.out.println("acl_domain: " + typedObject.getValue("acl_domain"));
                System.out.println("acl_name: " + typedObject.getValue("acl_name"));
                System.out.println("language_code: " + typedObject.getValue("language_code"));
                System.out.println("r_object_type: " + typedObject.getValue("r_object_type"));
                System.out.println("r_creation_date: " + typedObject.getValue("r_creation_date"));
                System.out.println("r_modify_date: " + typedObject.getValue("r_modify_date"));
                System.out.println("a_content_type: " + typedObject.getValue("a_content_type"));

                System.out.println("----------------------------------------");

            }
            collection.close();

            dql = "select user_name from dm_user where user_group_name='docu'";
            query = ClientXUtils.getQuery(dql);
            collection = query.execute(session, DfQuery.DF_READ_QUERY);
            while (collection.next()) {
                IDfTypedObject typedObject = collection.getTypedObject();
                System.out.println("user_name: " + typedObject.getValue("user_name"));
            }
            collection.close();
            System.out.println("----------------------------------------");
            dql = "select user_name from dm_user where user_group_name='admingroup'";
            query = ClientXUtils.getQuery(dql);
            collection = query.execute(session, DfQuery.DF_READ_QUERY);
            while (collection.next()) {
                IDfTypedObject typedObject = collection.getTypedObject();
                System.out.println("user_name: " + typedObject.getValue("user_name"));
            }
            collection.close();
            System.out.println("----------------------------------------");
            dql = "select object_name from dm_document(all) where owner_name='Administrator'";
            query = ClientXUtils.getQuery(dql);
            collection = query.execute(session, DfQuery.DF_READ_QUERY);
            while (collection.next()) {
                IDfTypedObject typedObject = collection.getTypedObject();
                System.out.println("object_name: " + typedObject.getValue("object_name"));
            }
            collection.close();
            System.out.println("----------------------------------------");
            dql = "select r_object_id,i_chronicle_id,object_name from dm_document(all) where folder('/Alibaba',descend)";
            query = ClientXUtils.getQuery(dql);
            collection = query.execute(session, DfQuery.DF_READ_QUERY);
            while (collection.next()) {
                IDfTypedObject typedObject = collection.getTypedObject();
                System.out.println("object_name: " + typedObject.getValue("object_name"));
                System.out.println("r_object_id: " + typedObject.getValue("r_object_id"));
                System.out.println("i_chronicle_id: " + typedObject.getValue("i_chronicle_id"));
            }
            collection.close();


        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail();
        } finally {
            if (session != null) {
                sessionManager.release(session);
            }

        }
    }

}
