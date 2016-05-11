// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IConfigLookup.java

package com.gihow.dfc;


// Referenced classes of package com.documentum.web.formext.config:
//			Context, IConfigElement

public interface IConfigLookup
{

	public abstract IConfigElement lookupElement(String s, Context context);

	public abstract String lookupString(String s, Context context);

	public abstract Boolean lookupBoolean(String s, Context context);

	public abstract Integer lookupInteger(String s, Context context);
}
