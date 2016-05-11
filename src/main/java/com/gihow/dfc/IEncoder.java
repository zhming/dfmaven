// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IEncoder.java

package com.gihow.dfc;


public interface IEncoder
{

	public abstract String encodeForJavascript(String s);

	public abstract String encodeForHTMLAttribute(String s);

	public abstract String encodeForHTML(String s);
}
