// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConnectionBroker.java

package com.gihow.dfc;

import com.gihow.util.HostUtil;

import java.io.Serializable;

public class ConnectionBroker
	implements Serializable
{

	private String m_strDocbrokerName;
	private String m_strPort;
	private static final String PORT_SEPARATOR = ":";
	public static final String DEFAULT_DOCBROKER_PORT = "1489";

	public ConnectionBroker()
	{
		m_strDocbrokerName = "";
		m_strPort = "1489";
	}

	public ConnectionBroker(String strDocbroker)
	{
		m_strDocbrokerName = "";
		m_strPort = "1489";
		m_strDocbrokerName = HostUtil.getHostName(strDocbroker);
		if (HostUtil.getPortNumber(strDocbroker).length() > 0)
			m_strPort = HostUtil.getPortNumber(strDocbroker);
	}

	public void setDocbrokerName(String docbrokerName)
	{
		m_strDocbrokerName = docbrokerName;
	}

	public void setDocbrokerPort(String port)
	{
		m_strPort = port;
	}

	public String getDocbrokerName()
	{
		return m_strDocbrokerName;
	}

	public String getDocbrokerPort()
	{
		return m_strPort;
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof ConnectionBroker)
		{
			ConnectionBroker docbroker = (ConnectionBroker)obj;
			return isEqual(docbroker.getDocbrokerName(), m_strDocbrokerName) && isEqual(docbroker.getDocbrokerPort(), m_strPort);
		} else
		{
			return super.equals(obj);
		}
	}

	public int hashCode()
	{
		int hashCode = 0;
		if (m_strDocbrokerName != null)
			hashCode += m_strDocbrokerName.hashCode();
		if (m_strPort != null)
			hashCode += m_strPort.hashCode();
		return hashCode;
	}

	public String asString()
	{
		StringBuffer bufResult = new StringBuffer(256);
		bufResult.append(m_strDocbrokerName);
		bufResult.append(":");
		bufResult.append(m_strPort);
		return bufResult.toString();
	}

	private boolean isEqual(String str1, String str2)
	{
		if (str1 == null && str2 == null)
			return true;
		if (str1 != null)
			return str1.equals(str2);
		else
			return false;
	}
}
