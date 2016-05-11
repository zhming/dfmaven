// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ITag.java

package com.gihow.dfc;

import java.util.Properties;

public interface ITag
{

	public static final String HTML_TAG = "html";
	public static final String HEAD_TAG = "head";
	public static final String TITLE_TAG = "title";
	public static final String BODY_TAG = "body";
	public static final String TABLE_TAG = "table";
	public static final String TR_TAG = "tr";
	public static final String TD_TAG = "td";
	public static final String TH_TAG = "th";
	public static final String SPAN_TAG = "span";
	public static final String DIV_TAG = "div";

	public abstract StringBuffer renderStartTag(String s, Properties properties, StringBuffer stringbuffer);

	public abstract StringBuffer renderEndTag(String s, Properties properties, StringBuffer stringbuffer);

	public abstract StringBuffer renderTagBody(String s, StringBuffer stringbuffer);
}
