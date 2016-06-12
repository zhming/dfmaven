package com.ecm.security.login;

import com.documentum.fc.client.DfUser;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.ecm.security.session.UserAccessor;
import com.ecm.security.session.UserAccessorAware;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class Signup extends ActionSupport implements ModelDriven, UserAccessorAware {
    private IDfUser user = new DfUser();
    private String verifyPassword;
    private UserAccessor ua;

    public IDfUser getUser() {
        return user;
    }

    public void setUser(IDfUser user) {
        this.user = user;
    }

    public String getVerifyPassword() {
        return verifyPassword;
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;
    }

    public Object getModel() {
        return user;
    }

    public void setUserAccessor(UserAccessor ua) {
        this.ua = ua;
    }

    public String execute() throws DfException {
        if (ua.getByUsername(user.getUserName()) == null) {
            ua.signup(user);
            return SUCCESS;
        } else {
            addFieldError("username", "Username is already taken, please choose another");
            return ERROR;
        }
    }
}
