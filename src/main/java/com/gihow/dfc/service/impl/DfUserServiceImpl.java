package com.gihow.dfc.service.impl;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.gihow.dfc.service.IDfUserService;
import com.gihow.dfc.sessionmananger.SessionManagerUtil;
import com.gihow.util.StaticValuesUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-16
 * Time: 下午3:55
 * To change this template use File | Settings | File Templates.
 */
public class DfUserServiceImpl implements IDfUserService {

    public IDfUser getUserByUsername(String username){
        IDfUser user = null;
        IDfSessionManager sMgr = null;
        IDfSession session = null;
        try{
            sMgr = SessionManagerUtil.createAdminSessionManager();
            session = sMgr.getSession(StaticValuesUtil.DOCBASE);
            user = session.getUser(username);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            SessionManagerUtil.release(sMgr, session);
        }
        return user;
    }

    @Override
    public IDfUser getUserById(String id)   {
        IDfUser user = null;
        IDfSessionManager sMgr = null;
        IDfSession session = null;
        try {
            sMgr = SessionManagerUtil.createSessionManager(StaticValuesUtil.GLOBAL_USERNAME, StaticValuesUtil.GLOBAL_PASSWORD);
            session = sMgr.getSession(StaticValuesUtil.DOCBASE);
            user = session.getUser(id);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException("sessionMananger 异常： " + e.getMessage());

        }finally {
            SessionManagerUtil.release(sMgr, session);
        }
        return user;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<IDfUser> getAllUsers() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void main(String[] args) throws Exception {
        IDfUserService userService = new DfUserServiceImpl();
        IDfUser user = userService.getUserByUsername("test");
        try {
            System.out.println(user.getObjectId() + " : " + user.getUserName());
            System.out.println(user.getObjectId() + " : " + user.getUserName());
        } catch (DfException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
