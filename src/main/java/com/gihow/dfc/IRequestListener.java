// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IRequestListener.java

package com.gihow.dfc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IRequestListener
{

	public abstract void notifyRequestStart(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse);

	public abstract void notifyRequestFinish(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse);
}
