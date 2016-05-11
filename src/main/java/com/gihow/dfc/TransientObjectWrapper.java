// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TransientObjectWrapper.java

package com.gihow.dfc;

import java.io.Serializable;

public class TransientObjectWrapper
	implements Serializable
{

	private final transient Object m_object;

	public TransientObjectWrapper(Object obj)
	{
		if (obj == null)
		{
			throw new NullPointerException();
		} else
		{
			m_object = obj;
			return;
		}
	}

	public Object get()
	{
		return m_object;
	}
}
