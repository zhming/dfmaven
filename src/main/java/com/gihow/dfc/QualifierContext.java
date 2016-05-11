// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   QualifierContext.java

package com.gihow.dfc;


// Referenced classes of package com.documentum.web.formext.config:
//			Context

public final class QualifierContext
{

	private Context m_context;

	public String get(String strContextName)
	{
		return m_context.get(strContextName);
	}

	public String toString()
	{
		return m_context.toString();
	}

	QualifierContext(Context context)
	{
		m_context = context;
	}
}
