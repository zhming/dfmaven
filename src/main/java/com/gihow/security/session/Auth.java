package com.gihow.security.session;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;
import com.gihow.dfc.Cache;
import com.gihow.dfc.SessionManagerHttpBinding;
import com.gihow.util.StaticValuesUtil;
import org.joda.time.DateTime;

import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-16
 * Time: 下午4:57
 * To change this template use File | Settings | File Templates.
 */
public class Auth {

    public static boolean authDfcSession( String strDocbase, String strUsername, String strPassword, String strDomain  )
            throws DfException {
        IDfSessionManager sessionManager;
        IDfSession session;
        IDfLoginInfo loginInfo = new DfLoginInfo();
        loginInfo.setUser(strUsername);
        loginInfo.setPassword(strPassword);
        if(strDomain != null && !strDomain.trim().equals("")){
            loginInfo.setDomain(strDomain);
        }
        boolean isLoginSuccess = false;
        sessionManager = SessionManagerHttpBinding.getSessionManager();
        session = null;
        try {
            SessionManagerHttpBinding.removeConnectedDocbase(strDocbase);
            if (sessionManager.hasIdentity(strDocbase))
                sessionManager.clearIdentity(strDocbase);
            sessionManager.setIdentity(strDocbase, loginInfo);
            session = sessionManager.getSession(strDocbase);
            String strUser = loginInfo.getUser();
            if (strUser != null && strUser.length() > 0)
                System.out.println((new StringBuilder()).append("AuthenticationService: Successfully log user '").append(loginInfo.getUser()).append("' into docbase '").append(strDocbase).append("'").toString());
            else
                System.out.println((new StringBuilder()).append("AuthenticationService: Successfully logged into docbase '").append(strDocbase).append("'").toString());
            isLoginSuccess = true;
        } catch (DfException dfe) {
            System.out.println((new StringBuilder()).append("AuthenticationService: Failed to log user '").append(loginInfo.getUser()).append("' into docbase '").append(strDocbase).append("', error=").append(dfe).toString());
            sessionManager.clearIdentity(strDocbase);
            throw dfe;
        } finally {
            if (session != null)
                sessionManager.release(session);
        }
        return isLoginSuccess;
    }

    public static boolean authDfc(String username, String password){
        boolean isLogin = false;
        DateTime starT = new DateTime();
        IDfClientX clientX = new DfClientX();
        IDfClient client = null;
        IDfSessionManager sMgr = null;
        IDfSession session = null;
        try{
            client = clientX.getLocalClient();
            IDfLoginInfo loginInfo = clientX.getLoginInfo();
            loginInfo.setUser(username);
            loginInfo.setPassword(password);
            System.out.println("消耗时间newSessionMananger： " + ((new DateTime()).getMillis() - starT.getMillis()));
            sMgr = client.newSessionManager();
            sMgr.setIdentity(StaticValuesUtil.DOCBASE, loginInfo);
            session = sMgr.getSession(StaticValuesUtil.DOCBASE);
            System.out.println("消耗时间getSession： " + ((new DateTime()).getMillis() - starT.getMillis()));
            if(session != null) isLogin = true;

        }catch (Exception e){
            e.printStackTrace();
        }if(session != null){
            sMgr.release(session);
        }
        DateTime endT = new DateTime();
        System.out.println("消耗时间authDfc： " + (endT.getMillis() - starT.getMillis()));
        return isLogin;
    }
}
