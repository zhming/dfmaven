// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HttpSessionState.java

package com.gihow.dfc;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Map;

// Referenced classes of package com.documentum.web.common:
//			SessionState, Trace

public class HttpSessionState extends SessionState
{
	private static class HttpSessionStore
		implements SessionState.ISessionStore
	{

		private transient HttpSession m_session;

		public String getSessionId()
		{
			return m_session.getId();
		}

		public void setAttribute(String strAttrName, Object oValue)
		{
			m_session.setAttribute(strAttrName, oValue);
		}

		public Object getAttribute(String strAttrName)
		{
			return m_session.getAttribute(strAttrName);
		}

		public void removeAttribute(String strAttrName)
		{
			m_session.removeAttribute(strAttrName);
		}

		public String toString()
		{
			StringBuffer buf = new StringBuffer(1024);
			buf.append((new StringBuilder()).append("HttpSessionStore [").append(getSessionId()).append("]:").toString());
			String strName;
			String strValue;
			for (Enumeration enumNames = m_session.getAttributeNames(); enumNames.hasMoreElements(); buf.append((new StringBuilder()).append("\n...").append(strName).append(" = ").append(strValue).toString()))
			{
				strName = (String)enumNames.nextElement();
				Object oValue = m_session.getAttribute(strName);
				if (oValue instanceof HttpSessionStore)
					strValue = com.gihow.dfc.HttpSessionState.class.getName();
				else
					strValue = oValue.toString();
			}

			return buf.toString();
		}

		public void verifySession(HttpSession session)
		{
			if (m_session == null)
				m_session = session;
		}


		public HttpSessionStore(HttpSession session)
		{
			m_session = session;
		}
	}


	private static final String SESSION_STORE = com.gihow.dfc.HttpSessionState.class.getName();
	public static final String IS_REAL_HTTP_SESSION = "IsRealHttpSession";

	public HttpSessionState()
	{
	}

	public static void bindHttpSession(HttpSession session)
	{
		SessionState.ISessionStore sessionStore = retrieveHttpSessionStore(session);
		registerSessionStore(sessionStore);
	}

	public static void releaseHttpSession(HttpSession session)
	{
		deregisterSessionStore();
	}

	public static void copySessionStore(Map toStore)
	{
		SessionState.ISessionStore sessionStore = getSessionStore();
		HttpSession httpSession = ((HttpSessionStore)sessionStore).m_session;
		Enumeration en = httpSession.getAttributeNames();
		do
		{
			if (!en.hasMoreElements())
				break;
			String name = (String)en.nextElement();
			if (!name.equals(SESSION_STORE))
				toStore.put(name, httpSession.getAttribute(name));
		} while (true);
		toStore.put("IsRealHttpSession", Boolean.FALSE);
	}

	private static SessionState.ISessionStore retrieveHttpSessionStore(HttpSession session)
	{
		SessionState.ISessionStore sessionStore = null;
		if (session == null)
			throw new IllegalArgumentException("session: mandatory parameter");
		synchronized (session)
		{
			sessionStore = (SessionState.ISessionStore)session.getAttribute(SESSION_STORE);
			if (sessionStore == null)
			{
				sessionStore = new HttpSessionStore(session);
				Boolean isRealHttpSession = (Boolean)session.getAttribute("IsRealHttpSession");
				if (isRealHttpSession == null || isRealHttpSession != null && isRealHttpSession.booleanValue())
				{
					session.setAttribute(SESSION_STORE, sessionStore);
					if (Trace.SESSIONSTATE)
						Trace.println((new StringBuilder()).append("SessionState: Created Session Store for HTTP Session ").append(session.getId()).toString());
				}
			} else
			if (sessionStore instanceof HttpSessionStore)
				((HttpSessionStore)sessionStore).verifySession(session);
		}
		return sessionStore;
	}

}
