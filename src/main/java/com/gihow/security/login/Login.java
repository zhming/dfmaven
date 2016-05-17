package com.gihow.security.login;

import com.documentum.fc.common.DfException;
import com.gihow.security.session.UserAccessorAware;
import com.opensymphony.xwork2.ActionContext;

import java.io.UnsupportedEncodingException;

public class Login extends LoginForm implements UserAccessorAware {
    
    public String execute() throws DfException{
    	if (ua.authenticate(getUsername(), getPassword())) {
    		setUser(ua.getByUsername(getUsername()));
    		if(getUser().getACLName().equalsIgnoreCase("default")){
    			addFieldError("username", "Sorry, your account not activated yet.");
    			return INPUT;
    		} else {
	            ActionContext.getContext().getSession().put(LoginFilter.LOGIN_GA_USER, su.encodeBase64(getUser().getObjectId().getId()));
	            return SUCCESS;
    		}
        } else {
                addFieldError("username","Invalid username or password.中文");
            return INPUT;
        }

    }

    @Override
    public void validate() {
        if(getUsername() == null || getUsername().equals("")){
            addFieldError("userName", "用户名不能为空");
        }
        if(getPassword() == null || getPassword().equals("")){
            addFieldError("password", "密码不能为空");
        }else{
                addActionMessage("Welcome " + getUsername() + ", You have been Successfully Logged in");
        }


    }
}