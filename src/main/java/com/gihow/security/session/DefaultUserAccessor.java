package com.gihow.security.session;


import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;
import com.gihow.dfc.LocaleService;
import com.gihow.dfc.PreferenceRepository;
import com.gihow.dfc.SessionManagerHttpBinding;
import com.gihow.dfc.Trace;
import com.gihow.persistence.PersistenceAware;
import com.gihow.persistence.PersistenceException;
import com.gihow.persistence.PersistenceManager;
import com.gihow.util.PropertyLooker;
import com.gihow.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class DefaultUserAccessor implements UserAccessor, PersistenceAware {
	private PersistenceManager pm;
	private StringUtils su = new StringUtils();
	
	public boolean authenticate(String username, String password) {
        IDfLoginInfo loginInfo = new DfLoginInfo();
        loginInfo.setUser(username);
        loginInfo.setPassword(password);
        IDfSessionManager sessionManager = SessionManagerHttpBinding.getSessionManager();
        IDfSession session = null;
        String strUser = "";
        try {
            session = sessionManager.getSession(PropertyLooker.get("application.preferencesrepository.repository"));
            sessionManager.setLocale(LocaleService.getLocale().toString());
            if (Trace.SESSION)
            {
                strUser = loginInfo.getUser();
                if (strUser != null && strUser.length() > 0)
                    Trace.println((new StringBuilder()).append("AuthenticationService: Successfully log user '").append(loginInfo.getUser()).append("' into docbase '").append(strDocbase).append("'").toString());
                else
                    Trace.println((new StringBuilder()).append("AuthenticationService: Successfully logged into docbase '").append(strDocbase).append("'").toString());
            }
        } catch (DfServiceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if(session != null){

            }
        }
		if (strUser == null || !strUser.trim().equals("")) {
			return false;
		} else {
			return true;
		}
	}

    public void login(HttpSession httpSession, String strDocbase, String strUsername, String strPassword, String strDomain, HttpServletRequest request)
            throws DfException
    {
        authDfcSession(httpSession, strDocbase, strUsername, strPassword, strDomain, true);
        if (SessionManagerHttpBinding.getConnectedDocbases().size() == 1)
        {
            HttpSession newHttpSession = changeSessionIdOnAuthentication(httpSession, request);
            authDfcSession(newHttpSession, strDocbase, strUsername, strPassword, strDomain, false);
        }
    }

	public IDfUser getByUsername(String username) {
		try {
			return (IDfUser) pm.getByUniqueField(IDfUser.class, username, "username");
		} catch (PersistenceException e) {
			return null;
		}
	}
	public void signup(IDfUser user) {
		pm.save(user);
	}
	public void setPersistenceManager(PersistenceManager pm) {
		this.pm = pm;
	}

	public void update(IDfUser userEdited) {
		pm.save(userEdited);
		
	}
	public IDfUser getById(String userId) {
		
		return (IDfUser) pm.getById(IDfUser.class, userId);
	}
	
	public List<IDfUser> findAllUser() {
        try {
			return pm.findAll(IDfUser.class);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

	public void delete(IDfUser user) {
		pm.remove(user);
	}
}
