package com.gihow.interceptors;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.gihow.GenerateMenu;
import com.gihow.entity.Descriptor;
import com.gihow.entity.ModuleFunction;
import com.gihow.entity.RolePrivilage;
import com.gihow.security.session.SessionCredentials;
import com.gihow.security.session.SessionCredentialsAware;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;

public class DefaultInterceptor implements Interceptor,
        SessionCredentialsAware {
	private SessionCredentials sessCredentials;
	private String siteId;
	private IDfACL currentRole;
	private IDfUser currentUser;
	private Descriptor descriptorCalled;

	public void destroy() {

	}

	public void init() {
		GenerateMenu.run();
	}

	public String intercept(ActionInvocation actionInvocation) throws Exception {
		if (sessCredentials.getCurrentUser() != null) {
			// init currentUser
			currentUser = sessCredentials.getCurrentUser();
			//currentRole = (IDfACL)currentUser.getACLName();
            currentRole = null;
			// init descriptorCalled
			String namespace = actionInvocation.getProxy().getNamespace();
			String descriptorCandidate[] = namespace.split("/");
			if ("module".equalsIgnoreCase(descriptorCandidate[1])) {
//				String descriptorName = descriptorCandidate[2];
//				descriptorCalled = (Descriptor) manager.getByUniqueField(
//						Descriptor.class, descriptorName, "name");
//				if (descriptorCalled != null) {
//					if (!isAuthorized(actionInvocation)) {
//						return "notallowed";
//					}
//				} else {
//					return "notallowed";
//				}
                return "notallowed";
			}
		}
		return actionInvocation.invoke();
	}

	private boolean isAuthorized(ActionInvocation actionInvocation) throws DfException {

		return false;
	}

	private boolean checkLeafDescriptor(ModuleFunction parent) {
		for (ModuleFunction mf : parent.getModuleFunctions()) {
			if(mf.getModuleFunctions().size()>0) {
				if (checkLeafDescriptor(mf)) {
					return true;
				}
			} else {
				if (descriptorCalled.equals(mf.getModuleDescriptor())) {
					return true;
				}
			} 
		}
		return false;
	}

	private List<ModuleFunction> getLeafPrivilage(ModuleFunction parent) {
		List<ModuleFunction> mfs = new ArrayList<ModuleFunction>();
		for (ModuleFunction mf : parent.getModuleFunctions()) {
			if (mf.getModuleFunctions().size() > 0) {
				mfs.addAll(getLeafPrivilage(mf));
			} else {
				mfs.add(mf);
			} 
		}
		return mfs;
	}

	public void setSessionCredentials(SessionCredentials arg0) {
		this.sessCredentials = arg0;
	}

}
