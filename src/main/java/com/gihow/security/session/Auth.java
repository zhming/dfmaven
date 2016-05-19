package com.gihow.security.session;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.gihow.dfc.sessionmananger.SessionManagerUtil;
import com.gihow.util.StaticValuesUtil;

/**
 *
 * 验证用户登录信息
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-16
 * Time: 下午4:57
 * To change this template use File | Settings | File Templates.
 */
public class Auth {

    /**
     *
     * @param username  用户名
     * @param password  密码
     * @return
     * @throws PasswordExpiredException
     */
    public static boolean authDfc(String username, String password) throws PasswordExpiredException {
        boolean isLogin = false;
        IDfSessionManager sMgr = null;
        IDfSession session = null;
        try {
            sMgr = SessionManagerUtil.createSessionManager(username, password);
            session = sMgr.getSession(StaticValuesUtil.DOCBASE);
            if(session != null) {
                isLogin = true;
                String strMessage = session.getMessage(3);
                if (strMessage != null && strMessage.indexOf("[DM_SESSION_E_PASSWORD_EXPIRED]") != -1)
                    throw new PasswordExpiredException(strMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }finally {
            SessionManagerUtil.release(sMgr, session);
        }

        return isLogin;
    }
}
