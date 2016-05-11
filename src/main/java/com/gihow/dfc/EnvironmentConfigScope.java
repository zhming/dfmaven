// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   EnvironmentConfigScope.java

package com.gihow.dfc;

import java.io.Serializable;

public class EnvironmentConfigScope
	implements Serializable
{

	private String m_strDesc;
	private String m_strDisplayName;
	private int m_nPriority;
	public static final EnvironmentConfigScope ADMIN = new EnvironmentConfigScope("Admin", "Admin", 0);
	public static final EnvironmentConfigScope GROUP_PROFILE = new EnvironmentConfigScope("Group-Profile");
	public static final EnvironmentConfigScope GROUP = new EnvironmentConfigScope("Group", "Group", 0);
	public static final EnvironmentConfigScope USER = new EnvironmentConfigScope("User", "User", 0);
	public static final EnvironmentConfigScope PORTLET = new EnvironmentConfigScope("Portlet", "Portlet", 0);

	public String toString()
	{
		return (new StringBuilder()).append("Configuration scope: ").append(m_strDesc).toString();
	}

	private EnvironmentConfigScope(String strDesc)
	{
		m_strDisplayName = null;
		m_nPriority = 0;
		m_strDesc = strDesc;
	}

	private EnvironmentConfigScope(String strDesc, String strDisplayName)
	{
		m_strDisplayName = null;
		m_nPriority = 0;
		m_strDesc = strDesc;
		m_strDisplayName = strDisplayName;
	}

	private EnvironmentConfigScope(String strDesc, String strDisplayName, int nPriority)
	{
		m_strDisplayName = null;
		m_nPriority = 0;
		m_strDesc = strDesc;
		m_strDisplayName = strDisplayName;
		m_nPriority = nPriority;
	}

	private EnvironmentConfigScope()
	{
		m_strDisplayName = null;
		m_nPriority = 0;
	}

	public String getDescription()
	{
		return m_strDesc;
	}

	public String getDisplayName()
	{
		return m_strDisplayName;
	}

	public int getPriority()
	{
		return m_nPriority;
	}

}
