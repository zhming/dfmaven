// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IHttpSessionManagerUnboundListener.java

package com.gihow.dfc;

import com.documentum.fc.client.IDfSessionManager;
import java.io.Serializable;

public interface IHttpSessionManagerUnboundListener
	extends Serializable
{

	public abstract void unbound(IDfSessionManager idfsessionmanager);
}
