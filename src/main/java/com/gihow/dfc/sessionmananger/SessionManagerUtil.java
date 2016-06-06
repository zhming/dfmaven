package com.gihow.dfc.sessionmananger;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.gihow.util.StaticValuesUtil;
import org.apache.log4j.Logger;

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
    private static Logger log = Logger.getLogger(SessionManagerUtil.class);

    private SessionManagerUtil(){
        super();
    }

    /**
     * 通过用户名、密码获取sessionmanager
     * @param user
     * @param pass
     * @return
     * @throws Exception
     */
    public static IDfSessionManager createSessionManager(String user, String pass) throws Exception  {
        clientX = new DfClientX();
        client = clientX.getLocalClient();
        sMgr = client.newSessionManager();
        IDfLoginInfo loginInfoObj = clientX.getLoginInfo();
        loginInfoObj.setUser(user);
        loginInfoObj.setPassword(pass);
        sMgr.setIdentity(StaticValuesUtil.DOCBASE, loginInfoObj);
        return sMgr;
    }

    /**
     * 直接获取一个超级用户权限的sessionmanager
     *
     * @return
     * @throws Exception
     */
    public static IDfSessionManager createAdminSessionManager() throws Exception  {
        clientX = new DfClientX();
        client = clientX.getLocalClient();
        sMgr = client.newSessionManager();
        IDfLoginInfo loginInfoObj = clientX.getLoginInfo();
        loginInfoObj.setUser(StaticValuesUtil.GLOBAL_USERNAME);
        loginInfoObj.setPassword(StaticValuesUtil.GLOBAL_PASSWORD);
        sMgr.setIdentity(StaticValuesUtil.DOCBASE, loginInfoObj);
        return sMgr;
    }

    public static void release(IDfSessionManager sMgr, IDfSession session){
        if(sMgr != null && session != null){
            sMgr.release(session);
        }
    }



    /**
     * 显示session最后一次执行的sql语句
     * @param session
     * @return
     */
    public static String getSqlString(IDfSession session){
        String sqlResult = null;
        try {
            IDfCollection collection = session.apply(null, "GET_LAST_SQL", null, null, null);
            if(collection.next()){
                sqlResult = collection.getString("result");
            }
            collection.close();
        } catch (DfException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return sqlResult;

    }

}
