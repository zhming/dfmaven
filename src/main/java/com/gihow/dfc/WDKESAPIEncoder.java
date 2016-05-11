// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   WDKESAPIEncoder.java

package com.gihow.dfc;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;

public class WDKESAPIEncoder
	implements IEncoder
{

	public WDKESAPIEncoder()
	{
	}

	public String encodeForJavascript(String toBeEncoded)
	{
		return ESAPI.encoder().encodeForJavaScript(toBeEncoded);
	}

	public String encodeForHTMLAttribute(String toBeEncoded)
	{
		return ESAPI.encoder().encodeForHTMLAttribute(toBeEncoded);
	}

	public String encodeForHTML(String toBeEncoded)
	{
		return ESAPI.encoder().encodeForHTML(toBeEncoded);
	}
}
