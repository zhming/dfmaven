package com.gihow.security.session;

import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;

import java.util.List;


public interface UserAccessor {
    boolean authenticate(String username, String password) throws DfException;

    void signup(IDfUser user);

    IDfUser getByUsername(String username);

    IDfUser getById(String userId);

	void update(IDfUser userEdited);
	
	void delete(IDfUser user);
	
	List findAllUser();
}
