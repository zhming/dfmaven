package com.gihow.security.session;


import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.gihow.persistence.PersistenceAware;
import com.gihow.persistence.PersistenceException;
import com.gihow.persistence.PersistenceManager;
import com.gihow.util.StringUtils;

import java.util.List;

public class DefaultUserAccessor implements UserAccessor, PersistenceAware {
	private PersistenceManager pm;
	private StringUtils su = new StringUtils();
	
	public boolean authenticate(String username, String password) throws DfException {
		IDfUser user = getByUsername(username);

		if (user == null || !user.getUserPassword().equals(su.encodeBase64(password))) {
			return false;
		} else {
			return true;
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
