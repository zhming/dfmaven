package com.ecm.security.session;

import com.documentum.fc.client.IDfUser;
import com.ecm.security.login.LoginFilter;
import com.ecm.util.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

public class HttpSessionCredentials implements SessionCredentials,
		UserAccessorAware {

	private UserAccessor userAccessor;

	public void setUserAccessor(UserAccessor userAccessor) {
		this.userAccessor = userAccessor;
	}

	public IDfUser getCurrentUser() {
		StringUtils stringUtils = new StringUtils();

		String remoteUser = getRequest().getRemoteUser();

		String userId = "";

		if (ServletActionContext.getRequest().getSession().getAttribute(
				LoginFilter.LOGIN_GA_USER) != null) {
			userId = stringUtils.decodeBase64(""
					+ ServletActionContext.getRequest().getSession()
							.getAttribute(LoginFilter.LOGIN_GA_USER));
		}

		if (userId.equalsIgnoreCase("")) {
			return null;
		} else {
			return userAccessor.getById(userId);
		}
	}

	private HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}
}