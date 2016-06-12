package com.ecm.dfc.sessionmananger;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.search.IDfSearchService;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfLoginInfo;
import com.ecm.util.StaticValuesUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-23
 * Time: 上午8:57
 * To change this template use File | Settings | File Templates.
 */
public final class ClientXUtils {

    private ClientXUtils() {
        super();
    }

    public static IDfId getId(String objectId) {
        return new DfClientX().getId(objectId);
    }

    public static IDfClientX getClientX() throws DfException {
        return new DfClientX();
    }

    public static IDfClient getClient() throws DfException {
        return getClientX().getLocalClient();
    }

    public static IDfSessionManager getSessionManager() throws DfException {
        return getClient().newSessionManager();
    }

    public static IDfLoginInfo getLoginInfo() throws DfException {
        return new DfClientX().getLoginInfo();
    }

    /**
     * 根据Session获取IDfSessionManager
     * @param session
     * @return
     * @throws DfException
     */
    public static IDfSessionManager newSessionManager(IDfSession session)
            throws DfException {
        IDfLoginInfo loginInfo = session.getLoginInfo();
        int timeout = session.getServerConfig().getInt(
                "max_login_ticket_timeout");
        System.out.println("timeout: " + timeout);
        loginInfo.setPassword(session.getLoginTicketEx(loginInfo.getUser(),
                "docbase", timeout, false, session.getDocbaseName()));
        System.out.println("timeout: " + timeout);
        IDfSessionManager sessionManager = getSessionManager();
        sessionManager.setIdentity(session.getDocbaseName(), loginInfo);
        return sessionManager;
    }

    /**
     * @param docbase
     * @param userName
     * @param password
     * @return
     * @throws DfException
     */
    public static IDfSessionManager getSessionManager(String docbase,
                                                      String userName, String password) throws DfException {
        return getSessionManager(docbase, userName, password, null);
    }

    public static IDfSessionManager getSessionManager(String docbase,
                                                      String userName, String password, String domain) throws DfException {
        IDfSessionManager sessionManager = getSessionManager();
        sessionManager.setIdentity(docbase,
                getLoginInfo(userName, password, domain));
        return sessionManager;
    }

    public static IDfSessionManager getSessionManager(String userName, String password) throws DfException {
        IDfSessionManager sessionManager = getSessionManager();
        sessionManager.setIdentity(StaticValuesUtil.DOCBASE,
                getLoginInfo(userName, password, null));
        return sessionManager;
    }

    public static IDfLoginInfo getLoginInfo(String user, String password)
            throws DfException {
        return getLoginInfo(user, password, null);
    }

    public static IDfLoginInfo getLoginInfo(String user, String password,
                                            String domain) throws DfException {
        IDfLoginInfo loginInfo = getLoginInfo();
        loginInfo.setUser(user);
        loginInfo.setPassword(password);
        loginInfo.setDomain(domain);
        return loginInfo;
    }

    public static IDfQuery getQuery(String dql) throws DfException {
        IDfQuery query = new DfClientX().getQuery();
        query.setDQL(dql);
        return query;
    }

    public static IDfList getList() throws DfException {
        return new DfClientX().getList();
    }

    public static IDfSearchService getSearchService(String userName, String password) throws DfException {
        return new DfClientX().getLocalClient().newSearchService(getSessionManager(userName, password), StaticValuesUtil.DOCBASE);
    }


}

