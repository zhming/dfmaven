// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IApplicationListener.java

package com.gihow.dfc;

import javax.servlet.ServletContext;

public interface IApplicationListener
{

	public abstract void notifyApplicationStart(ServletContext servletcontext);

	public abstract void notifyApplicationFinish(ServletContext servletcontext);
}
