// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   PrimaryElementsByLocationDictionary.java

package com.gihow.dfc;

import java.util.HashMap;

// Referenced classes of package com.documentum.web.formext.config:
//			IConfigElement

class PrimaryElementsByLocationDictionary
	implements Cloneable
{

	private HashMap m_dictionary;
	private static final Object s_nullSentinel = new Object();

	public PrimaryElementsByLocationDictionary(int initSize)
	{
		m_dictionary = new HashMap(initSize);
	}

	public synchronized IConfigElement get(String strPrimaryElement, String strLocation)
	{
		IConfigElement primaryElement = null;
		String strLookupKey = constructLookupKey(strPrimaryElement, strLocation);
		Object value = m_dictionary.get(strLookupKey);
		if (value != null && (value instanceof IConfigElement))
			primaryElement = (IConfigElement)value;
		return primaryElement;
	}

	public synchronized void put(String strPrimaryElement, String strLocation, IConfigElement primaryElement)
	{
		Object value = primaryElement;
		String strLookupKey = constructLookupKey(strPrimaryElement, strLocation);
		Object existingValue = m_dictionary.get(strLookupKey);
		if (existingValue != null)
			value = s_nullSentinel;
		m_dictionary.put(strLookupKey, value);
	}

	private String constructLookupKey(String strPrimaryElement, String strLocation)
	{
		StringBuffer buf = new StringBuffer(90);
		buf.append(strPrimaryElement).append(':');
		strLocation = strLocation.replace('\\', '/');
		if (strLocation.charAt(0) == '/')
			strLocation = strLocation.substring(1);
		buf.append(strLocation);
		if (!strLocation.endsWith(".xml"))
			buf.append(".xml");
		return buf.toString();
	}

	public void trace()
	{
		StringBuffer buf = new StringBuffer(128);
		buf.append("PrimaryElementsByLocationDictionary, size: ");
		buf.append(m_dictionary.size());
		Trace.println(buf.toString());
	}

	protected Object clone()
	{
		PrimaryElementsByLocationDictionary clone = null;
        try {
            clone = (PrimaryElementsByLocationDictionary)super.clone();
            clone.m_dictionary = (HashMap)m_dictionary.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

		return clone;
	}

	synchronized void remove(String strPrimaryElement, String strLocation)
	{
		String strLookupKey = constructLookupKey(strPrimaryElement, strLocation);
		m_dictionary.remove(strLookupKey);
	}

}
