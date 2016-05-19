package com.gihow.action.main;

import com.documentum.fc.client.IDfUser;
import com.gihow.DefaultAction;
import com.gihow.dfc.service.IDfUserService;
import com.gihow.security.login.LoginFilter;
import com.gihow.util.StringUtils;
import com.opensymphony.xwork2.ActionContext;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-19
 * Time: 上午11:19
 * To change this template use File | Settings | File Templates.
 */


public class MainAction extends DefaultAction {
    private StringUtils su = new StringUtils();
    private IDfUserService userService;
    @Override
    public String execute() throws Exception {
        if (ActionContext.getContext().getSession().get(
                LoginFilter.LOGIN_GA_USER) != null) { // sudah login
            IDfUser user = userService.getUserById(su.decodeBase64(ActionContext.getContext().getSession().get(LoginFilter.LOGIN_GA_USER).toString()));


            return "continue";
        } else { // belum login
            return "login";
        }
    }

    public IDfUserService getUserService() {
        return userService;
    }

    public void setUserService(IDfUserService userService) {
        this.userService = userService;
    }
}
