package com.gihow.security.login;

import com.opensymphony.xwork2.ActionContext;

public class Logout extends LoginForm {

    public String execute() {
        ActionContext.getContext().getSession().remove(LoginFilter.LOGIN_GA_USER);
        return SUCCESS;
    }

}
