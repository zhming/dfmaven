// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ClientSessionState.java

package com.gihow.dfc;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ClientSessionState
{

	private static final String LAST_CLIENT_ID_USED = "lastClientIdUsed";
	private static final String TEMP_CLIENT_ID = "tempClientId";
	private static final String CLIENT_STORE = "clientStore";
	private static final String REUSED_TEMP_STORE = "reusedTempStore";
	private static final String NOT_APPLICABLE = "na";
	private static final String CLIENT_SESSION_STATE_FLAG = "application.client-sessionstate.enabled";
	private static ThreadLocal s_clientId = new ThreadLocal();
	private static ThreadLocal s_clientSesseionStateEnabled = new ThreadLocal();

	public ClientSessionState()
	{
	}

	public static String getSessionId()
	{
		return SessionState.getSessionId();
	}

	public static void setAttribute(String strAttrName, Object oValue)
	{
		setAttribute(getClientId(), strAttrName, oValue);
	}

	public static void setAttribute(String browserId, String strAttrName, Object oValue)
	{
		if (s_clientSesseionStateEnabled.get() == null)
			SessionState.setAttribute(strAttrName, oValue);
		else
			getClientStore(browserId, true).put(strAttrName, oValue);
	}

	public static Object getAttribute(String strAttrName)
	{
		return getAttribute(getClientId(), strAttrName);
	}

	public static Object getAttribute(String browserId, String strAttrName)
	{
		if (s_clientSesseionStateEnabled.get() == null)
			return SessionState.getAttribute(strAttrName);
		else
			return getClientStore(browserId, true).get(strAttrName);
	}

	public static void removeAttribute(String strAttrName)
	{
		if (s_clientSesseionStateEnabled.get() == null)
			SessionState.removeAttribute(strAttrName);
		else
			getClientStore(getClientId(), true).remove(strAttrName);
	}

	public static void bindHttpRequest(HttpServletRequest request)
	{
		boolean applicable = false;
		HttpSession session = null;
		if (request != null)
		{
			session = request.getSession();
			applicable = isClientSessionStateEnabled(session);
		}
		if (!applicable)
		{
			s_clientId.set("na");
			s_clientSesseionStateEnabled.set(null);
		} else
		{
			boolean tryReuseTempClientStore = false;
			String strClientId = request.getParameter("__dmfClientId");
			if (strClientId == null || strClientId.length() == 0)
			{
				strClientId = (String)session.getAttribute("lastClientIdUsed");
				if (strClientId == null || strClientId.length() == 0)
					strClientId = generateTempClientId(session);
				if (Trace.CLIENTSESSIONSTATE)
					Trace.println((new StringBuilder()).append("CLIENTSESSIONSTATE: binding previous saved client Id = ").append(strClientId).append(" for request URL = ").append(request.getRequestURL()).toString());
			} else
			{
				tryReuseTempClientStore = true;
				session.setAttribute("lastClientIdUsed", strClientId);
				if (Trace.CLIENTSESSIONSTATE)
					Trace.println((new StringBuilder()).append("CLIENTSESSIONSTATE: binding Client Id = ").append(strClientId).append(" for request URL = ").append(request.getRequestURL()).toString());
			}
			s_clientId.set(strClientId);
			s_clientSesseionStateEnabled.set(Boolean.TRUE);
			if (tryReuseTempClientStore && session.getAttribute("reusedTempStore") == null)
				reuseTempClientStore(session);
		}
	}

	public static void releaseHttpRequest(HttpServletRequest request)
	{
		s_clientId.set(null);
		s_clientSesseionStateEnabled.set(null);
	}

	public static String getClientId()
	{
		return (String)s_clientId.get();
	}

	private static Hashtable getClientStore(String clientId, boolean forceCreate)
	{
		String storeId = (new StringBuilder()).append(clientId).append("clientStore").toString();
		Hashtable store = (Hashtable)SessionState.getAttribute(storeId);
		if (store == null && forceCreate)
		{
			store = new Hashtable();
			SessionState.setAttribute(storeId, store);
		}
		return store;
	}

	private static void setClientStore(String clientId, Hashtable store)
	{
		String storeId = (new StringBuilder()).append(clientId).append("clientStore").toString();
		SessionState.setAttribute(storeId, store);
	}

	private static String generateTempClientId(HttpSession session)
	{
		String strClientId = null;
		synchronized (session)
		{
			strClientId = (String)session.getAttribute("lastClientIdUsed");
			if (strClientId == null || strClientId.length() == 0)
			{
				strClientId = "tempClientId";
				getClientStore("tempClientId", true);
			}
		}
		return strClientId;
	}

	private static void reuseTempClientStore(HttpSession session)
	{
		synchronized (session)
		{
			if (session.getAttribute("reusedTempStore") == null)
			{
				session.setAttribute("reusedTempStore", Boolean.TRUE);
				Hashtable tempClientStore = getClientStore("tempClientId", false);
				if (tempClientStore != null)
				{
					setClientStore(getClientId(), tempClientStore);
					if (Trace.CLIENTSESSIONSTATE)
						Trace.println((new StringBuilder()).append("CLIENTSESSIONSTATE: reuse temp client store for client Id = ").append(getClientId()).toString());
				}
			}
		}
	}

	private static boolean isClientSessionStateEnabled(HttpSession session)
	{
		Boolean enabled = (Boolean)session.getAttribute("application.client-sessionstate.enabled");
		if (enabled == null)
		{
			IConfigLookup lookup = ConfigService.getConfigLookup();
			String strEnabled = lookup.lookupString("application.client-sessionstate.enabled", new Context());
			if ("true".equals(strEnabled))
				enabled = Boolean.valueOf(true);
			else
				enabled = Boolean.valueOf(false);
			session.setAttribute("application.client-sessionstate.enabled", enabled);
		}
		return enabled.booleanValue();
	}

}
