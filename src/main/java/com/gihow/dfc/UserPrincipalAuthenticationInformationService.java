// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   UserPrincipalAuthenticationInformationService.java

package com.gihow.dfc;

import java.io.Serializable;

public class UserPrincipalAuthenticationInformationService
	implements Serializable
{

	private boolean m_bApplied;
	private boolean m_bAttempted;
	private static String USER_PRINCIPAL_AUTH_INFO_SERVICE = "user.principal.auth.info.service";

	public UserPrincipalAuthenticationInformationService()
	{
		m_bApplied = false;
		m_bAttempted = false;
	}

	public static synchronized UserPrincipalAuthenticationInformationService getService()
	{
		UserPrincipalAuthenticationInformationService service = null;
		if ((UserPrincipalAuthenticationInformationService)SessionState.getAttribute(USER_PRINCIPAL_AUTH_INFO_SERVICE) != null)
		{
			service = (UserPrincipalAuthenticationInformationService)SessionState.getAttribute(USER_PRINCIPAL_AUTH_INFO_SERVICE);
		} else
		{
			service = new UserPrincipalAuthenticationInformationService();
			SessionState.setAttribute(USER_PRINCIPAL_AUTH_INFO_SERVICE, service);
		}
		return service;
	}

	public boolean isApplied()
	{
		return m_bApplied;
	}

	public void setApplied(boolean applied)
	{
		m_bApplied = applied;
	}

	public boolean hasAttempted()
	{
		return m_bAttempted;
	}

	public void attempted()
	{
		m_bAttempted = true;
	}

}
