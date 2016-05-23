package com.gihow;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfUser;
import com.gihow.security.session.SessionCredentials;
import com.gihow.security.session.SessionCredentialsAware;
import com.gihow.util.PropertyLooker;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.URLBean;

import java.io.IOException;
import java.util.Properties;


public class DefaultAction extends ActionSupport implements SessionCredentialsAware {
	private SessionCredentials sessionCredentials;
	private String currDescriptor;
	
	private static Properties properties = new Properties();
	static {
		try {
			properties.load(PropertyLooker.getResourceAsStream("conf.properties"));
		} catch (IOException e){
			e.printStackTrace();
		} catch (NullPointerException npe) {
			LOG.info("file conf.properties is not in classpath");
			npe.printStackTrace();
		}
	}
	public static String getCurrDescriptorUrl(){
		URLBean bean = new URLBean();
		bean.setRequest(ServletActionContext.getRequest());
		bean.setResponse(ServletActionContext.getResponse());
		String target = ServletActionContext.getResponse().encodeRedirectURL(bean.toString());
		
		String descriptorCandidate[] = target.split("/");
		String descriptorName = descriptorCandidate[3];
		return descriptorName;
	}
	public static String get(String propertyName){
		return properties.getProperty(propertyName);
	}
	
	public void setSessionCredentials(SessionCredentials sessionCredentials) {
		this.sessionCredentials = sessionCredentials;
	}
	
	public IDfUser getCurrentUser(){
		return sessionCredentials.getCurrentUser();
	}
	public IDfACL getCurrentRole(){
		//return getCurrentUser().getACLName();
        return null;
	}
	public String getCurrDescriptor() {
		return currDescriptor;
	}
	public void setCurrDescriptor(String currDescriptor) {
		this.currDescriptor = currDescriptor;
	}
	public String getLogo(){
		return get("application.logo.url");
	}
}
