// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IAuthenticationServiceEx2.java

package com.gihow.dfc;

import com.documentum.fc.common.DfException;

import javax.servlet.http.HttpSession;

// Referenced classes of package com.documentum.web.formext.session:
//			IAuthenticationServiceEx

public interface IAuthenticationServiceEx2
	extends IAuthenticationServiceEx
{

	public abstract void login(HttpSession httpsession, String s, String s1)
		throws DfException;
}
