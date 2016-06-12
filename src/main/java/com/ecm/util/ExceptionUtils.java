// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ExceptionUtils.java

package com.ecm.util;

import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils
{

	public ExceptionUtils()
	{
	}

	public static Throwable getTailCause(Throwable e)
	{
		Throwable t = e;
		Throwable cause;
		for (cause = null; (t = getNextCause(t)) != null; cause = t);
		return cause;
	}

	public static boolean anyCauseMessageContains(Throwable e, String str)
	{
		Throwable t = e;
		do
		{
			String msg = t.getMessage();
			if (msg != null && msg.indexOf(str) != -1)
				return true;
		} while ((t = getNextCause(t)) != null);
		return false;
	}

	public static Throwable getNextCause(Throwable e)
	{
		Throwable cause = e.getCause();
		if (cause == null)
			if (e instanceof SAXException)
				cause = ((SAXException)e).getException();
			else
			if (e instanceof ServletException)
				cause = ((ServletException)e).getRootCause();
		return cause;
	}

	public static String getFullStackTrace(Throwable t)
	{
		StringBuffer buf = new StringBuffer();
		Throwable tt = t;
		do
		{
			String st = getStackTrace(tt);
			if (buf.indexOf(st) == -1)
				buf.append(st);
		} while ((tt = getNextCause(tt)) != null);
		return buf.toString();
	}

	private static String getStackTrace(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}
}
