package com.gihow.security.session;


import com.documentum.fc.client.IDfUser;
import com.gihow.dfc.service.IDfUserService;
import com.gihow.dfc.service.impl.DfUserServiceImpl;
import com.gihow.persistence.PersistenceAware;
import com.gihow.persistence.PersistenceException;
import com.gihow.persistence.PersistenceManager;

import java.util.*;

public class DefaultUserAccessor implements UserAccessor, PersistenceAware {
	private PersistenceManager pm;

	public boolean authenticate(String username, String password) throws PasswordExpiredException {
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
            IDfUserService userService = new DfUserServiceImpl();
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
