// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IQualifier.java

package com.gihow.dfc;


// Referenced classes of package com.documentum.web.formext.config:
//			QualifierContext

public interface IQualifier
{

	public abstract String[] getContextNames();

	public abstract String getScopeName();

	public abstract String getScopeValue(QualifierContext qualifiercontext);

	public abstract String getParentScopeValue(String s);

	public abstract String[] getAliasScopeValues(String s);
}
