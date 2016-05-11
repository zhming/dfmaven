// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IConfigLookupHookEx.java

package com.gihow.dfc;


// Referenced classes of package com.documentum.web.formext.config:
//			Context, IConfigElement

public interface IConfigLookupHookEx
{

	public abstract IConfigElement onLookupElement(String s, Context context);

	public abstract String onLookupString(String s, Context context);

	public abstract Boolean onLookupBoolean(String s, Context context);

	public abstract Integer onLookupInteger(String s, Context context);
}
