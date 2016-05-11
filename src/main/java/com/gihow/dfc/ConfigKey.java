// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConfigKey.java

package com.gihow.dfc;


// Referenced classes of package com.documentum.web.formext.config:
//			ScopeKey

final class ConfigKey
	implements Comparable
{

	public String m_primElem;
	public ScopeKey m_scopeKey;

	public ConfigKey(String primElem, ScopeKey scopeKey)
	{
		m_primElem = primElem;
		m_scopeKey = scopeKey;
	}

	public String getPrimaryElem()
	{
		return m_primElem;
	}

	public String getScopeValue(int index)
	{
		return m_scopeKey.m_strScopeValues[index];
	}

	public String[] getScopeValues()
	{
		return m_scopeKey.m_strScopeValues;
	}

	public String toString()
	{
		return (new StringBuilder()).append(m_primElem).append(":").append(m_scopeKey.toString()).toString();
	}

	public int compareTo(Object o)
	{
		ConfigKey ck = (ConfigKey)o;
		int retval = m_primElem.compareTo(ck.m_primElem);
		if (retval == 0)
			retval = m_scopeKey.compareTo(ck.m_scopeKey);
		return retval;
	}

	public boolean equals(Object o)
	{
		if (o instanceof ConfigKey)
		{
			ConfigKey ck = (ConfigKey)o;
			return m_primElem.equals(ck.m_primElem) && m_scopeKey.equals(ck.m_scopeKey);
		} else
		{
			return false;
		}
	}

	public int hashCode()
	{
		long hc = m_primElem.hashCode();
		hc += m_scopeKey.hashCode();
		return (int)(hc & -1L);
	}

	ConfigKey[] split()
	{
		ScopeKey multiScopeKeys[] = m_scopeKey.split();
		ConfigKey multiKeys[] = null;
		if (multiScopeKeys != null)
		{
			multiKeys = new ConfigKey[multiScopeKeys.length];
			for (int i = 0; i < multiScopeKeys.length; i++)
				multiKeys[i] = new ConfigKey(getPrimaryElem(), multiScopeKeys[i]);

		}
		return multiKeys;
	}
}
