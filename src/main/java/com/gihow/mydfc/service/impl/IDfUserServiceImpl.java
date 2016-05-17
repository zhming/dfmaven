package com.gihow.mydfc.service.impl;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.gihow.mydfc.service.IDfUserService;
import com.gihow.mydfc.sessionmananger.SessionManagerUtil;
import com.gihow.util.StaticValuesUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-16
 * Time: 下午3:55
 * To change this template use File | Settings | File Templates.
 */
public class IDfUserServiceImpl implements IDfUserService {
    private IDfSession session;

    @Override
    public IDfUser getUserByUsername(String username) throws Exception {
        IDfUser user = null;
        IDfSessionManager sMgr = SessionManagerUtil.createSessionManager(StaticValuesUtil.DOCBASE, StaticValuesUtil.GLOBAL_USERNAME, StaticValuesUtil.GLOBAL_PASSWORD);

        try {
            session = sMgr.getSession(StaticValuesUtil.DOCBASE);
            user = session.getUser(username);
            System.out.println(session.getSessionId() + " : " + session.getMessage(3));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException("sessionMananger 异常： " + e.getMessage());

        }finally {
             if(session != null){
                 sMgr.release(session);
             }
        }
        return user;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public IDfUser getUserById(String id) throws Exception {
        IDfUser user = null;
        IDfSessionManager sMgr = SessionManagerUtil.createSessionManager(StaticValuesUtil.DOCBASE, StaticValuesUtil.GLOBAL_USERNAME, StaticValuesUtil.GLOBAL_PASSWORD);
        try {


            session = sMgr.getSession(StaticValuesUtil.DOCBASE);
            user = session.getUser(id);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException("sessionMananger 异常： " + e.getMessage());

        }finally {
            sMgr.release(session);
        }
        return user;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<IDfUser> getAllUsers() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDfSession getSession() {
        return session;
    }

    public void setSession(IDfSession session) {
        this.session = session;
    }

    public static void main(String[] args) throws Exception {
        IDfUserService userService = new IDfUserServiceImpl();
        IDfUser user = userService.getUserByUsername("test");
        try {
            System.out.println(user.getObjectId() + " : " + user.getUserName());
            System.out.println(user.getObjectId() + " : " + user.getUserName());
        } catch (DfException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
