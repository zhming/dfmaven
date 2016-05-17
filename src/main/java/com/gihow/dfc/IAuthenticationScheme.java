// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IAuthenticationScheme.java

package com.gihow.dfc;

import com.documentum.fc.common.DfException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IAuthenticationScheme
{

	public abstract String authenticate(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, String s)
		throws DfException;

	public abstract String getLoginComponent(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, String s, ArgumentList argumentlist);
}
