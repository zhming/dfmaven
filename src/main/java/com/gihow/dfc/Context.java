// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Context.java

package com.gihow.dfc;

import java.io.Serializable;
import java.util.*;

// Referenced classes of package com.documentum.web.formext.config:
//			ClientEnvQualifier

public final class Context
	implements Serializable
{

	private HashMap m_items;
	private Context m_parent;
	private String m_strDesc;
	private static Context m_appContext = initRootContext();
	private static final String SESSION_CONFIG_CONTEXT = "SessionConfigContext";
	private static final String ELEMENT_SEPARATOR = "E*E*";
	private static final String VALUE_SEPARATOR = "V*V*";
	private static final String CONTEXT_DESC = "D*D*";
	private static final String CONTEXT_PARENT = "P*P*";

	public Context()
	{
		m_items = new HashMap(7, 1.0F);
		m_parent = getSessionContext();
		m_strDesc = "REQUEST";
	}

	public Context(Context context)
	{
		m_items = (HashMap)context.m_items.clone();
		m_parent = context.m_parent;
		m_strDesc = context.m_strDesc;
	}

	public static Context getSessionContext()
	{
		Context context = (Context)SessionState.getAttribute("SessionConfigContext");
		if (context == null)
		{
			if (m_appContext == null)
				m_appContext = initRootContext();
			context = new Context("SESSION", m_appContext);
			SessionState.setAttribute("SessionConfigContext", context);
		}
		return context;
	}


    public static void main(String[] args){
        Context context = Context.getSessionContext();
        System.out.println(context.get("config"));
        System.out.println(context.toString());

    }
	private static Context initRootContext()
	{
		return new Context("conf", null);
	}

	public static Context getApplicationContext()
	{
		return m_appContext;
	}

	public static String serialize(Context context)
	{
		StringBuffer buf = new StringBuffer();
		buf.append("D*D*");
		buf.append("V*V*");
		buf.append(context.m_strDesc);
		if (!context.m_items.isEmpty())
		{
			String strValue;
			for (Iterator iterKeys = context.m_items.keySet().iterator(); iterKeys.hasNext(); buf.append(strValue))
			{
				String strKey = (String)iterKeys.next();
				strValue = (String)context.m_items.get(strKey);
				buf.append("E*E*");
				buf.append(strKey);
				buf.append("V*V*");
			}

		}
		if (context.m_parent != null)
		{
			String strParent = serialize(context.m_parent);
			if (strParent != null)
			{
				buf.append("E*E*");
				buf.append("P*P*");
				buf.append(strParent);
			}
		}
		return buf.toString();
	}

	public static Context deserialize(String strContext)
	{
		ArrayList arrElements = getSerializedElements(strContext);
		String strParent = getSerializedParent(strContext);
		Context conParent = null;
		if (strParent != null)
			conParent = deserialize(strParent);
		Context context = new Context();
		context.m_parent = conParent;
		if (arrElements.size() > 0)
		{
			Iterator iterElements = arrElements.iterator();
			do
			{
				if (!iterElements.hasNext())
					break;
				String strElement = (String)iterElements.next();
				String strName = getElementName(strElement);
				String strValue = getElementValue(strElement);
				if (strName != null && strValue != null)
					if (strName.equals("D*D*"))
						context.m_strDesc = strValue;
					else
						context.set(strName, strValue);
			} while (true);
		}
		return context;
	}

	public void set(String strName, String strValue)
	{
		m_items.put(strName, strValue);
	}

	public void remove(String strName)
	{
		m_items.remove(strName);
	}

	public String toString()
	{
		StringBuffer strBuf = new StringBuffer(256);
		for (Context context = this; context != null; context = context.m_parent)
		{
			strBuf.append(context.m_strDesc);
			strBuf.append('(');
			boolean bFirst = true;
			String strValue;
			for (Iterator itemNames = context.m_items.keySet().iterator(); itemNames.hasNext(); strBuf.append(strValue))
			{
				if (!bFirst)
					strBuf.append(',');
				bFirst = false;
				String strName = (String)itemNames.next();
				strValue = (String)context.m_items.get(strName);
				if (strValue == null)
					strValue = "";
				strBuf.append(strName);
				strBuf.append('=');
			}

			strBuf.append(')');
		}

		return strBuf.toString();
	}

	public String get(String strName, Object obj)
	{
		return getOnBehalfOfFriend(strName, obj);
	}

//	public String get(String strName, com.documentum.web.formext.control.ClientEnvPanelTag.Friend friend)
//	{
//		return getOnBehalfOfFriend(strName, friend);
//	}

	/**
	 * @deprecated Method get is deprecated
	 */

//	public String get(String strName, IActionPrecondition friend)
//	{
//		return null;
//	}
//
//	public String get(String strName, ClientEnvQualifier.Friend friend)
//	{
//		return getOnBehalfOfFriend(strName, friend);
//	}

	private String getOnBehalfOfFriend(String strName, Object friend)
	{
		if (friend == null)
			throw new IllegalArgumentException("Context.get(...) - friend argument is required");
		else
			return get(strName);
	}

	String get(String strName)
	{
		String strValue = null;
		Context context = this;
		do
		{
			strValue = (String)context.m_items.get(strName);
			context = context.m_parent;
		} while (strValue == null && context != null);
		return strValue;
	}

	private Context(String strDesc, Context parent)
	{
		m_items = new HashMap(7, 1.0F);
		m_parent = parent;
		m_strDesc = strDesc;
	}

	private static ArrayList getSerializedElements(String strContext)
	{
		ArrayList arrElements = new ArrayList();
		if (strContext != null)
		{
			String strElements = null;
			int nIndex = strContext.indexOf("P*P*");
			if (nIndex > 0)
				strElements = strContext.substring(0, nIndex - "E*E*".length());
			else
				strElements = strContext;
			if (nIndex != 0)
			{
				for (int nToken = strElements.indexOf("E*E*"); nToken >= 0; nToken = strElements.indexOf("E*E*"))
				{
					arrElements.add(strElements.substring(0, nToken));
					strElements = strElements.substring(nToken + "E*E*".length());
				}

				arrElements.add(strElements);
			}
		}
		return arrElements;
	}

	private static String getSerializedParent(String strContext)
	{
		String strResult = null;
		if (strContext != null)
		{
			int nIndex = strContext.indexOf("P*P*");
			if (nIndex >= 0)
				strResult = strContext.substring(nIndex + "P*P*".length());
		}
		return strResult;
	}

	private static String getElementName(String strElement)
	{
		String strResult = null;
		if (strElement != null)
		{
			int nIndex = strElement.indexOf("V*V*");
			if (nIndex >= 0)
				strResult = strElement.substring(0, nIndex);
		}
		return strResult;
	}

	private static String getElementValue(String strElement)
	{
		String strResult = null;
		if (strElement != null)
		{
			int nIndex = strElement.indexOf("V*V*");
			if (nIndex >= 0)
				strResult = strElement.substring(nIndex + "V*V*".length());
		}
		return strResult;
	}

}
