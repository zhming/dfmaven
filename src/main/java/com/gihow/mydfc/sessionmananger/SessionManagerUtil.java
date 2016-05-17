package com.gihow.mydfc.sessionmananger;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.IDfLoginInfo;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-16
 * Time: 下午3:09
 * To change this template use File | Settings | File Templates.
 */
public class SessionManagerUtil {
    private static IDfClient client;
    private static IDfLoginInfo loginInfo;
    private static IDfClientX clientX;
    private static IDfSessionManager sMgr;
    private IDfSession session;

    public static IDfSessionManager createSessionManager(String docbase, String user, String pass) throws Exception  {
        //create a Client object
        clientX = new DfClientX();
        client = clientX.getLocalClient();
        //create a Session Manager object
        sMgr = client.newSessionManager();
        //create an IDfLoginInfo object named �loginInfoObj�
        IDfLoginInfo loginInfoObj = clientX.getLoginInfo();
        loginInfoObj.setUser(user);
        loginInfoObj.setPassword(pass);
        //bind the Session Manager to the login info
        sMgr.setIdentity(docbase, loginInfoObj);
        return sMgr;
    }

}
