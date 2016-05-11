// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IDispatch.java

package com.gihow.dfc;

import javax.servlet.http.HttpServletRequest;

public interface IDispatch
{

	/**
	 * @deprecated Method useForwardingDispatch is deprecated
	 */

	public abstract boolean useForwardingDispatch();

	/**
	 * @deprecated Method getRequestWrapper is deprecated
	 */

	public abstract HttpServletRequest getRequestWrapper(HttpServletRequest httpservletrequest);

	/**
	 * @deprecated Method dispatchInline is deprecated
	 */

	public abstract boolean dispatchInline();

	public abstract boolean useComponentController();
}
