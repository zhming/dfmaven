// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SECURITY.java

package com.gihow.dfc;


public class SECURITY
{

	private static IValidator m_validator = new WDKESAPIValidator();
	private static IEncoder m_encoder = new WDKESAPIEncoder();

	public SECURITY()
	{
	}

	public static IValidator validator()
	{
		return m_validator;
	}

	public static IEncoder encoder()
	{
		return m_encoder;
	}

}
