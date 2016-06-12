package com.ecm.service.impl;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.ecm.service.IDfUserService;
import com.ecm.dfc.sessionmananger.SessionManagerUtil;
import com.ecm.util.StaticValuesUtil;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-16
 * Time: 下午3:55
 * To change this template use File | Settings | File Templates.
 */
public class DfUserServiceImpl implements IDfUserService {
    private static Logger log = Logger.getLogger(DfUserServiceImpl.class);
    public IDfUser getUserByUsername(String username){
        log.debug("username: " + username);
        IDfUser user = null;
        IDfSessionManager sMgr = null;
        IDfSession session = null;
        try{
            sMgr = SessionManagerUtil.createAdminSessionManager();
            session = sMgr.getSession(StaticValuesUtil.DOCBASE);
            user = session.getUser(username);
        }catch (Exception e){
            log.debug(e.getMessage());
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
            log.debug(e.getMessage());
            throw new RuntimeException("sessionMananger 异常： " + e.getMessage());

        }finally {
            SessionManagerUtil.release(sMgr, session);
        }
        return user;
    }

    @Override
    public List<IDfUser> getAllUsers() {
        return null;
    }

    public static void main(String[] args) throws Exception {
        IDfUserService userService = new DfUserServiceImpl();
        IDfUser user = userService.getUserByUsername("Perry");
        try {
            log.debug(user.getObjectId() + " : " + user.getUserName());
            log.debug(user.getObjectId() + " : " + user.getUserPassword());
        } catch (DfException e) {
            log.debug(e.getMessage());
        }
    }
}
