package com.gihow.security.login;

import com.documentum.fc.client.DfACL;
import com.documentum.fc.client.DfUser;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.gihow.DefaultAction;
import com.gihow.security.session.UserAccessor;
import com.gihow.security.session.UserAccessorAware;
import com.gihow.util.StringUtils;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.ServletActionContext;
import org.owasp.esapi.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginForm extends DefaultAction implements UserAccessorAware {
	protected UserAccessor ua;
	protected StringUtils su = new StringUtils();
	private String username = "";
	private String password = "";
	private IDfUser user = new DfUser();
	private IDfACL role = new DfACL();
	private List<User> users = new ArrayList<User>();
	private String redirectUri;

	public String execute() throws DfException {
        System.out.println("LoginForm.............");
        System.out.println("username: " + username);
        System.out.println("password: " + password);
    	if(getRedirectUri()!=null&&!"".equalsIgnoreCase(getRedirectUri().trim())){
    		try {
				getResponse().sendRedirect("../../");
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		if (ActionContext.getContext().getSession().get(
				LoginFilter.LOGIN_GA_USER) != null) { // sudah login

//			getUser().setId(su.decodeBase64(ActionContext.getContext().getSession().get(LoginFilter.LOGIN_GA_USER).toString()));
//			setUser(ua.getById(getUser().getId()));

			return "continue";
		} else { // belum login
			return "login";
		}
	}

	public void setUserAccessor(UserAccessor ua) {
		this.ua = ua;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public IDfUser getUser() {
		return user;
	}

	public void setUser(IDfUser user) {
		this.user = user;
	}

	public StringUtils getSu() {
		return su;
	}

	public void setSu(StringUtils su) {
		this.su = su;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
	
	public boolean getAllowRegister(){
		return Boolean.getBoolean(get("application.registration.public"));
	}

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	public IDfACL getRole() {
		return role;
	}

	public void setRole(IDfACL role) {
		this.role = role;
	}
	
    public HttpServletResponse getResponse() {
		return (HttpServletResponse) ActionContext.getContext().get(ServletActionContext.HTTP_RESPONSE);
	}
	
	
}