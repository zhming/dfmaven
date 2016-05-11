// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Repository.java

package com.gihow.dfc;

import bsh.StringUtil;

import java.io.Serializable;

// Referenced classes of package com.documentum.web.formext.repository:
//			ConnectionBroker

public class Repository
	implements Serializable
{

	private String m_strRepositoryName;
	private ConnectionBroker m_docbroker;
	private static final String REPOSITORY_SEPARATOR = ",";
	public static final String DEFAULT_DOCBROKER_PORT = "1489";

	public Repository()
	{
		m_strRepositoryName = "";
		m_docbroker = null;
	}

	public Repository(String strRepository)
	{
		m_strRepositoryName = "";
		m_docbroker = null;
		if (strRepository == null)
			return;
		String strDocbrokerAndRepository[] = StringUtil.split(strRepository, ",");
		m_docbroker = new ConnectionBroker(strDocbrokerAndRepository[0]);
		if (strDocbrokerAndRepository.length >= 2)
			m_strRepositoryName = strDocbrokerAndRepository[1];
	}

	public void setRepositoryName(String repositoryName)
	{
		m_strRepositoryName = repositoryName;
	}

	public void setDocbrokerName(String docbrokerName)
	{
		getDocbroker().setDocbrokerName(docbrokerName);
	}

	public void setDocbrokerPort(String port)
	{
		getDocbroker().setDocbrokerPort(port);
	}

	public String getRepositoryName()
	{
		return m_strRepositoryName;
	}

	public String getDocbrokerName()
	{
		return getDocbroker().getDocbrokerName();
	}

	public String getDocbrokerPort()
	{
		return getDocbroker().getDocbrokerPort();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof Repository)
		{
			Repository repository = (Repository)obj;
			return isEqual(repository.m_strRepositoryName, m_strRepositoryName) && isEqual(repository.m_docbroker, m_docbroker);
		} else
		{
			return super.equals(obj);
		}
	}

	public int hashCode()
	{
		int hashCode = 0;
		if (m_strRepositoryName != null)
			hashCode += m_strRepositoryName.hashCode();
		if (m_docbroker != null)
			hashCode += m_docbroker.hashCode();
		return hashCode;
	}

	public String asString()
	{
		StringBuffer bufResult = new StringBuffer(256);
		bufResult.append(getDocbroker().asString());
		bufResult.append(",");
		bufResult.append(m_strRepositoryName);
		return bufResult.toString();
	}

	private boolean isEqual(Object obj1, Object obj2)
	{
		if (obj1 == null && obj2 == null)
			return true;
		if (obj1 != null)
			return obj1.equals(obj2);
		else
			return false;
	}

	private ConnectionBroker getDocbroker()
	{
		if (m_docbroker == null)
			m_docbroker = new ConnectionBroker();
		return m_docbroker;
	}
}
