// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BoundedContextCache.java

package com.gihow.dfc;

import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.documentum.web.formext.config:
//			ScopeKey, IQualifier, QualifierContext, Context

public final class BoundedContextCache
{

	private static final String NULL_STRING = "null marker";
	private Map m_context2ScopeKey;
	private Map m_qualifier2Value;
	private boolean m_trackContext;
	private boolean m_allowClear;
	private static ThreadLocal s_caches = new ThreadLocal();

	private BoundedContextCache()
	{
		m_context2ScopeKey = new HashMap();
		m_qualifier2Value = new HashMap();
		m_trackContext = false;
		m_allowClear = true;
	}

	public static boolean conditionalStart()
	{
		boolean started = false;
		if (getInstance() == null)
		{
			s_caches.set(new BoundedContextCache());
			started = true;
		}
		return started;
	}

	public static void end()
	{
		BoundedContextCache cache = getHardInstance();
		cache.release();
		s_caches.remove();
	}

	public static BoundedContextCache getInstance()
	{
		return (BoundedContextCache)s_caches.get();
	}

	public static boolean isStarted()
	{
		return getInstance() != null;
	}

	public void trackContext(boolean track)
	{
		m_trackContext = track;
		if (!track)
			m_context2ScopeKey.clear();
	}

	public boolean isContextTracked()
	{
		return m_trackContext;
	}

	ScopeKey getScopeKey(Context context)
	{
		return (ScopeKey)m_context2ScopeKey.get(context);
	}

	void setScopeKey(Context context, ScopeKey scopeKey)
	{
		m_context2ScopeKey.put(context, scopeKey);
	}

	String retrieveQualifierScopeValue(IQualifier qualifier, QualifierContext context)
	{
		String contextNames[] = qualifier.getContextNames();
		if (contextNames != null && (contextNames.length > 1 || contextNames.length == 1 && context.get(contextNames[0]) != null))
			return qualifier.getScopeValue(context);
		String scopeValue = (String)m_qualifier2Value.get(qualifier);
		if (scopeValue == null)
		{
			scopeValue = qualifier.getScopeValue(context);
			if (scopeValue == null)
				m_qualifier2Value.put(qualifier, "null marker");
			else
				m_qualifier2Value.put(qualifier, scopeValue);
		} else
		if (scopeValue == "null marker")
			scopeValue = null;
		return scopeValue;
	}

	void clear()
	{
		if (m_allowClear)
			release();
	}

	void setAllowClear(boolean flag)
	{
		m_allowClear = flag;
	}

	boolean getAllowClear()
	{
		return m_allowClear;
	}

	private static BoundedContextCache getHardInstance()
	{
		BoundedContextCache cache = getInstance();
		if (cache == null)
			throw new IllegalStateException("BoundedContextCache has never been started");
		else
			return cache;
	}

	private void release()
	{
		m_context2ScopeKey.clear();
		m_qualifier2Value.clear();
	}

}
