package com.ecm.security.session;


import com.documentum.fc.client.IDfUser;
import com.ecm.service.IDfUserService;

import java.util.*;

public class DefaultUserAccessor implements UserAccessor {


    private  IDfUserService  userService;

	public boolean authenticate(String username, String password) throws PasswordExpiredException {
        try {
            return Auth.authDfc(username, password);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }
    }

    //使用超级用户根据用户名获取用户实例
    public IDfUser getByUsername(String username) {
		try {
			return userService.getUserByUsername(username);
		} catch (Exception e) {
			return null;
		}
	}
	public void signup(IDfUser user) {
	}

	public void update(IDfUser userEdited) {

	}
	public IDfUser getById(String userId) {
		return null;
	}
	
	public List<IDfUser> findAllUser() {
        return null;
    }

	public void delete(IDfUser user) {
	}

    public IDfUserService getUserService() {
        return userService;
    }

    public void setUserService(IDfUserService userService) {
        this.userService = userService;
    }
}
