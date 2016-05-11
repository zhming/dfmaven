// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ISessionListener.java

package com.gihow.dfc;

import javax.servlet.http.HttpSession;

public interface ISessionListener
{

	public abstract void notifySessionStart(HttpSession httpsession);

	public abstract void notifySessionFinish(HttpSession httpsession);
}
