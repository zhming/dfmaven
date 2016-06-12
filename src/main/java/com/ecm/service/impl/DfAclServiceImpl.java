package com.ecm.service.impl;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.ecm.service.IDfAclService;
import com.ecm.dfc.sessionmananger.SessionManagerUtil;
import com.ecm.util.StaticValuesUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-19
 * Time: 下午3:06
 * To change this template use File | Settings | File Templates.
 */
public class DfAclServiceImpl implements IDfAclService {
    @Override
    public IDfACL getAclByName(String domain, String aclName) {
        IDfACL acl = null;
        IDfSessionManager sMgr = null;
        IDfSession session = null;
        try {
            sMgr = SessionManagerUtil.createAdminSessionManager();
            session = sMgr.getSession(StaticValuesUtil.DOCBASE);
            acl = session.getACL(domain, aclName);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return acl;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
