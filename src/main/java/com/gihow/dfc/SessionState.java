// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SessionState.java

package com.gihow.dfc;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

// Referenced classes of package com.documentum.web.common:
//			ThreadLocalVariable, Trace

public class SessionState
	implements Serializable
{
	protected static class ThreadSessionStore
		implements ISessionStore
	{

		private String m_threadName;
		private int m_iInitialCapacity;
		private HashMap m_map;
		private static final int INITIAL_CAPACITY = 7;

		public String getSessionId()
		{
			return m_threadName;
		}

		public void setAttribute(String strAttrName, Object oValue)
		{
			if (m_map == null)
				m_map = new HashMap(m_iInitialCapacity);
			m_map.put(strAttrName, oValue);
		}

		public Object getAttribute(String strAttrName)
		{
			Object oValue = null;
			if (m_map != null)
				oValue = m_map.get(strAttrName);
			return oValue;
		}

		public void removeAttribute(String strAttrName)
		{
			if (m_map != null)
				m_map.remove(strAttrName);
		}

		public String toString()
		{
			StringBuffer buf = new StringBuffer(1024);
			buf.append((new StringBuilder()).append("ThreadSessionStore [").append(getSessionId()).append("]:\n").toString());
			String strName;
			Object oValue;
			for (Iterator iterNames = m_map.keySet().iterator(); iterNames.hasNext(); buf.append((new StringBuilder()).append("Attribute ").append(strName).append(" = ").append(oValue.toString()).append("\n").toString()))
			{
				strName = (String)iterNames.next();
				oValue = m_map.get(strName);
			}

			return buf.toString();
		}

		public ThreadSessionStore(Thread thread)
		{
			this(thread, 7);
		}

		public ThreadSessionStore(Thread thread, int iInitialCapacity)
		{
			m_map = null;
			if (thread == null)
			{
				throw new IllegalArgumentException("thread is a mandatory parameter");
			} else
			{
				m_threadName = thread.getName();
				m_iInitialCapacity = iInitialCapacity;
				return;
			}
		}
	}

	protected static interface ISessionStore
		extends Serializable
	{

		public abstract String getSessionId();

		public abstract void setAttribute(String s, Object obj);

		public abstract Object getAttribute(String s);

		public abstract void removeAttribute(String s);
	}


	private static ThreadLocalVariable s_sessionStore = new ThreadLocalVariable("SessionStore");

	public SessionState()
	{
	}

	public static String getSessionId()
	{
		ISessionStore sessionStore = getSessionStore();
		if (sessionStore == null)
		{
			Thread currentThread = Thread.currentThread();
			sessionStore = new ThreadSessionStore(currentThread);
			registerSessionStore(sessionStore);
		}
		return sessionStore.getSessionId();
	}

	public static void setAttribute(String strAttrName, Object oValue)
	{
		if (strAttrName == null || strAttrName.length() == 0)
			throw new IllegalArgumentException("strAttrName is a mandatory parameter");
		ISessionStore sessionStore = getSessionStore();
		if (sessionStore == null)
		{
			Thread currentThread = Thread.currentThread();
			sessionStore = new ThreadSessionStore(currentThread);
			registerSessionStore(sessionStore);
		}
		sessionStore.setAttribute(strAttrName, oValue);
		if (Trace.SESSIONSTATE)
			Trace.println((new StringBuilder()).append("SessionState: [").append(sessionStore.getSessionId()).append("] Set ").append(strAttrName).append(" = ").append(oValue).toString());
	}

	public static Object getAttribute(String strAttrName)
	{
		Object oValue = null;
		ISessionStore sessionStore = getSessionStore();
		if (sessionStore != null)
		{
			oValue = sessionStore.getAttribute(strAttrName);
			if (Trace.SESSIONSTATE)
			{
				Boolean bEnabled = (Boolean)sessionStore.getAttribute("trace-enabled");
				if (bEnabled == null)
					bEnabled = Boolean.valueOf(Trace.SESSIONENABLEDBYDEFAULT);
				if (bEnabled.booleanValue())
				{
					StringBuffer buf = new StringBuffer(256);
					buf.append("SessionState: ");
					if (sessionStore != null)
						buf.append((new StringBuilder()).append("[").append(sessionStore.getSessionId()).append("] ").toString());
					buf.append((new StringBuilder()).append("Get ").append(strAttrName).append(" = ").append(oValue).toString());
					try
					{
						Trace.println(buf.toString());
					}
					catch (IllegalArgumentException e) { }
				}
			}
		}
		return oValue;
	}

	public static Object getTransientAttribute(String attributeName)
	{
		TransientObjectWrapper wrapper = (TransientObjectWrapper)getAttribute(attributeName);
		return wrapper == null ? null : wrapper.get();
	}

	public static void setTransientAttribute(String strAttrName, Object oValue)
	{
		setAttribute(strAttrName, new TransientObjectWrapper(oValue));
	}

	public static void removeAttribute(String strAttrName)
	{
		ISessionStore sessionStore = getSessionStore();
		if (sessionStore != null)
			sessionStore.removeAttribute(strAttrName);
		if (Trace.SESSIONSTATE)
		{
			StringBuffer buf = new StringBuffer(256);
			buf.append("SessionState: ");
			if (sessionStore != null)
				buf.append((new StringBuilder()).append("[").append(sessionStore.getSessionId()).append("] ").toString());
			buf.append((new StringBuilder()).append("Remove ").append(strAttrName).toString());
			Trace.println(buf.toString());
		}
	}

	protected static ISessionStore getSessionStore()
	{
		ISessionStore sessionStore = (ISessionStore)s_sessionStore.getThreadValue();
		return sessionStore;
	}

	protected static void registerSessionStore(ISessionStore sessionStore)
	{
		s_sessionStore.setThreadValue(sessionStore);
	}

	protected static void deregisterSessionStore()
	{
		s_sessionStore.setThreadValue(null);
	}

}
