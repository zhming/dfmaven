// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   PrimaryElementsByScopeDictionary.java

package com.gihow.dfc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

// Referenced classes of package com.documentum.web.formext.config:
//			ScopedDictionary, IConfigElement, ConfigKey, ScopeKey, 
//			ConfigService, IQualifier

class PrimaryElementsByScopeDictionary
	implements Cloneable
{

	private String m_strName;
	private ScopedDictionary m_dictionary;

	public PrimaryElementsByScopeDictionary(String strName, int initSize)
	{
		m_strName = strName;
		m_dictionary = new ScopedDictionary(initSize);
	}

	public void init(IQualifier qualifiers[])
	{
		if (qualifiers != null)
			m_dictionary.setQualifiers(qualifiers);
		else
			throw new RuntimeException("Scope Dictionary initialized with null qualifiers");
	}

	public synchronized IConfigElement get(String strPrimaryElement, ScopeKey scopeKey)
	{
		if (Trace.CONFIGSERVICE)
			Trace.println((new StringBuilder()).append("\tLookup dictionary '").append(m_strName).append("': Resolving scope").toString());
		IConfigElement primaryElement = null;
		ConfigKey ck = createLookupKey(strPrimaryElement, scopeKey);
		Object o = m_dictionary.get(ck);
		if (o != null)
			if (ScopedDictionary.NULL_SENTINEL.equals(o))
				primaryElement = null;
			else
				primaryElement = (IConfigElement)o;
		return primaryElement;
	}

	public synchronized IConfigElement getAbsolute(String strPrimaryElement, ScopeKey scopeKey)
	{
		ConfigKey lookupKey = createLookupKey(strPrimaryElement, scopeKey);
		Object object = m_dictionary.getExact(lookupKey);
		if (!ScopedDictionary.NULL_SENTINEL.equals(object))
			return (IConfigElement)object;
		else
			return null;
	}

	public synchronized void put(String strPrimaryElement, ScopeKey scopeKey, IConfigElement primaryElement)
	{
		ConfigKey lookupKey = createLookupKey(strPrimaryElement, scopeKey);
		Object existingEntry = m_dictionary.getExact(lookupKey);
		if (existingEntry != null)
		{
			String strMsg = null;
			String filePath = null;
			if (existingEntry instanceof IConfigElement)
			{
				filePath = ConfigService.getConfigFilePathName((IConfigElement) existingEntry);
				strMsg = (new StringBuilder()).append("Lookup dictionary '").append(m_strName).append("': Duplicate element '").append(strPrimaryElement).append("', scope='").append(scopeKey.toString()).append("', config files='").append(ConfigService.getConfigFilePathName(primaryElement)).append("' and '").append(filePath).append("'").toString();
			} else
			if (existingEntry == ScopedDictionary.NULL_SENTINEL)
				strMsg = (new StringBuilder()).append("Lookup dictionary '").append(m_strName).append("': Duplicate element '").append(strPrimaryElement).append("', scope='").append(scopeKey.toString()).append("', duplicate config file='").append(ConfigService.getConfigFilePathName(primaryElement)).append("', existing config entry is a placeholder indicating the element is explicitly undefined.").toString();
			throw new IllegalStateException(strMsg);
		}
		boolean bNotDefined = ConfigService.isMarkedAsNotDefined(primaryElement);
		if (scopeKey.m_bUndefine && bNotDefined)
			throw new IllegalStateException((new StringBuilder()).append("Lookup dictionary '").append(m_strName).append("': Cannot use 'notdefined' within scope that includes 'not' keyword: ").append(primaryElement.toString()).toString());
		if (scopeKey.m_bUndefine | bNotDefined)
		{
			m_dictionary.put(lookupKey, ScopedDictionary.NULL_SENTINEL);
			if (Trace.CONFIGSERVICE)
				Trace.println((new StringBuilder()).append("\tLookup dictionary '").append(m_strName).append("': Clearing =").append(strPrimaryElement).append(", scope='").append(scopeKey.toString()).append("', element='").append(primaryElement.toString()).append("'").toString());
		} else
		{
			m_dictionary.put(lookupKey, primaryElement);
			if (Trace.CONFIGSERVICE)
				Trace.println((new StringBuilder()).append("\tLookup dictionary '").append(m_strName).append("': Setting =").append(strPrimaryElement).append(", scope='").append(scopeKey.toString()).append("', config file='").append(primaryElement.toString()).append("'").toString());
		}
	}

	public synchronized void remove(String strPrimaryElement, ScopeKey scopeKey)
	{
		ConfigKey lookupKey = createLookupKey(strPrimaryElement, scopeKey);
		Object existingEntry = m_dictionary.getExact(lookupKey);
		if (existingEntry == null)
			return;
		m_dictionary.remove(lookupKey);
		if (Trace.CONFIGSERVICE)
			Trace.println((new StringBuilder()).append("\tLookup dictionary '").append(m_strName).append("': Removing =").append(strPrimaryElement).append(", scope='").append(scopeKey.toString()).toString());
	}

	public String[] getPrimaryElementIds(String strElementName)
	{
		Set ids = new HashSet(127);
		Iterator iterKeys = m_dictionary.keySet().iterator();
		do
		{
			if (!iterKeys.hasNext())
				break;
			ConfigKey key = (ConfigKey)iterKeys.next();
			String strKey = key.toString();
			if (strKey.startsWith(strElementName))
			{
				int iOpenId = strKey.indexOf("[id=");
				int iCloseId = strKey.indexOf("]");
				if (iOpenId == strElementName.length() && iCloseId > iOpenId && (m_dictionary.get(key) instanceof IConfigElement))
				{
					String strId = strKey.substring(iOpenId + 4, iCloseId);
					ids.add(strId);
				}
			}
		} while (true);
		String strIds[] = new String[ids.size()];
		ids.toArray(strIds);
		return strIds;
	}

	public String[] getPrimaryElementScopes(String strElementName)
	{
		IConfigElement elements[] = getPrimaryElements(strElementName);
		String strScopes[] = new String[elements.length];
		for (int i = 0; i < elements.length; i++)
			strScopes[i] = ConfigService.getPrimaryElementScopeEx(elements[i]);

		return strScopes;
	}

	public IConfigElement[] getPrimaryElements(String strElementName)
	{
		LinkedList elements = new LinkedList();
		Iterator iterKeys = m_dictionary.keySet().iterator();
		do
		{
			if (!iterKeys.hasNext())
				break;
			ConfigKey key = (ConfigKey)iterKeys.next();
			if (key.m_primElem.equals(strElementName))
			{
				Object obj = m_dictionary.get(key);
				if (obj instanceof IConfigElement)
					elements.add(obj);
			}
		} while (true);
		IConfigElement arrElements[] = new IConfigElement[elements.size()];
		elements.toArray(arrElements);
		return arrElements;
	}

	public void trace()
	{
		StringBuffer buf = new StringBuffer(128);
		buf.append("PrimaryElementsByScopeDictionary (");
		buf.append(m_strName);
		buf.append("), size: ");
		buf.append(m_dictionary.size());
		Trace.println(buf.toString());
	}

	private ConfigKey createLookupKey(String strPrimaryElement, ScopeKey scopeKey)
	{
		return new ConfigKey(strPrimaryElement, scopeKey);
	}

	protected Object clone()
	{
		PrimaryElementsByScopeDictionary clone = null;
        try {
            clone = (PrimaryElementsByScopeDictionary)super.clone();
            clone.m_dictionary = (ScopedDictionary)m_dictionary.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

		return clone;

	}
}
