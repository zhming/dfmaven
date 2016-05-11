// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ThreadLocalVariable.java

package com.gihow.dfc;


// Referenced classes of package com.documentum.web.common:
//			ThreadLocalCache

public final class ThreadLocalVariable
{

	private String m_name;
	private static final int THREAD_LOCALE_UNIQUE_KEY = "com.documentum.web.common.ThreadLocalVariable.".hashCode();
	private static final String NO_NAME = "no name assigned";

	public ThreadLocalVariable(String strName)
	{
		m_name = (new StringBuilder()).append(THREAD_LOCALE_UNIQUE_KEY).append(".").append(strName).toString();
	}

	public ThreadLocalVariable()
	{
		this("no name assigned");
	}

	public void setThreadValue(Object oValue)
	{
		ThreadLocalCache.put(m_name, oValue);
	}

	public Object getThreadValue()
	{
		return ThreadLocalCache.get(m_name);
	}

}
