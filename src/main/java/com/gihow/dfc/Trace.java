// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Trace.java

package com.gihow.dfc;


// Referenced classes of package com.documentum.web.common:
//			SessionState

public class Trace extends com.gihow.debug.Trace
{

	public static boolean SESSIONENABLEDBYDEFAULT;
	public static boolean SESSIONSYNC;
	public static boolean SESSIONSTATE;
	public static boolean THREADLOCALVARIABLE;
	public static boolean LOCALESERVICE;
	public static boolean BRANDINGSERVICE;
	public static boolean FAILOVER;
	public static boolean FAILOVER_DIAGNOSTICS;
	public static boolean COOKIE;
	public static boolean CLIENTNETWORKLOCATION;
	public static boolean COLLABORATIONSERVICE;
	public static boolean AUTOCOMPLETESERVICE;
	public static boolean IMAGINGSERVICE;
	public static boolean HOTKEYSSERVICE;
	public static boolean PRESET;
	public static boolean RECORDSMANAGER;
	public static boolean CLUSTERING;
	public static boolean SEARCH;
	public static boolean BREADCRUMBSERVICE;

    public static boolean SESSION;
    public static boolean CLIENTSESSIONSTATE;
    public static boolean CLIENTDOCBASE;
    public static boolean CONFIGSERVICE;
    public static boolean CONFIGMODIFICATION;
    public static boolean RESPONSE_HEADER_CONTROL;

	public Trace()
	{
	}

	public static void setTracingEnabled(boolean bEnabled)
	{
		SessionState.setAttribute("trace-enabled", Boolean.valueOf(bEnabled));
	}

	public static boolean isTracingEnabled()
	{
		Boolean bEnabled = null;
		try
		{
			bEnabled = (Boolean)SessionState.getAttribute("trace-enabled");
		}
		catch (Throwable e) { }
		if (bEnabled == null)
			bEnabled = Boolean.valueOf(SESSIONENABLEDBYDEFAULT);
		return bEnabled.booleanValue();
	}

	public static void println(String strMessage)
	{
		if (isTracingEnabled())
			com.gihow.debug.Trace.println(com.gihow.dfc.Trace.class, strMessage);
	}

	public static void error(String strMessage, Throwable t)
	{
		com.gihow.debug.Trace.error(com.gihow.dfc.Trace.class, strMessage, t);
	}

	public static void fatal(String strMessage, Throwable t)
	{
		com.gihow.debug.Trace.fatal(com.gihow.dfc.Trace.class, strMessage, t);
	}
}
