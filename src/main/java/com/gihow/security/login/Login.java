package com.gihow.security.login;

import com.documentum.fc.common.DfException;
import com.gihow.security.session.UserAccessorAware;
import com.opensymphony.xwork2.ActionContext;

public class Login extends LoginForm implements UserAccessorAware {
    
    public String execute() throws DfException{
        System.out.println("login ..............................");
    	if (ua.authenticate(getUsername(), getPassword())) {
    		setUser(ua.getByUsername(getUsername()));
    		if(getUser().getACLName().equalsIgnoreCase("default")){
    			addFieldError("username", "Sorry, your account not activated yet.");
    			return INPUT;
    		} else {
	            ActionContext.getContext().getSession().put(LoginFilter.LOGIN_GA_USER, su.encodeBase64(getUser().getObjectId().getId()));
	            
	            /* Normal flow */
	            return SUCCESS;
    		}
        } else {
            addFieldError("username", "Invalid username or password.");
            return INPUT;
        }
    }

    
}