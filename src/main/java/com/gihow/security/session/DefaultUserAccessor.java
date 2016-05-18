package com.gihow.security.session;


import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;
import com.gihow.dfc.*;
import com.gihow.mydfc.service.IDfUserService;
import com.gihow.mydfc.service.impl.IDfUserServiceImpl;
import com.gihow.persistence.PersistenceAware;
import com.gihow.persistence.PersistenceException;
import com.gihow.persistence.PersistenceManager;
import com.gihow.util.PropertyLooker;
import com.gihow.util.StaticValuesUtil;
import com.gihow.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

public class DefaultUserAccessor implements UserAccessor, PersistenceAware {
	private PersistenceManager pm;

	public boolean authenticate(String username, String password) {
        try {
            //return Auth.authDfcSession(StaticValuesUtil.DOCBASE, username, password, "");
            return Auth.authDfc(username, password);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
    }

    //使用超级用户根据用户名获取用户实例
    public IDfUser getByUsername(String username) {
		try {
            IDfUserService userService = new IDfUserServiceImpl();
			return userService.getUserByUsername(username);
		} catch (Exception e) {
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
