// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IAuthenticationService.java

package com.gihow.dfc;

import com.documentum.fc.common.DfException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Referenced classes of package com.documentum.web.formext.session:
//			PasswordExpiredException

public interface IAuthenticationService
{

	public abstract String authenticate(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, String s)
		throws DfException;

	public abstract String getLoginComponent(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, String s, ArgumentList argumentlist);

	public abstract void login(HttpSession httpsession, String s, String s1, String s2, String s3)
		throws  DfException;

	public abstract void changePassword(HttpSession httpsession, String s, String s1, String s2, String s3, String s4)
		throws DfException;
}
