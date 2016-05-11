// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space
// Source File Name:   WrapperRuntimeException.java

package com.gihow.dfc;


import com.gihow.util.ExceptionUtils;

import java.io.PrintStream;
import java.io.PrintWriter;

// Referenced classes of package com.documentum.web.common:
//			Trace, ErrorMessageService

public class WrapperRuntimeException extends RuntimeException
{

	private static ThreadLocal s_inCallStack = new ThreadLocal();
	private Throwable m_except;
	private String m_strDetails;

	public WrapperRuntimeException(String strMessage)
	{
		this(strMessage, null);
	}

	public WrapperRuntimeException(Throwable e)
	{
		this(null, e);
	}

	public WrapperRuntimeException(String strMessage, Throwable e)
	{
		super(strMessage, e);
		m_except = null;
		m_strDetails = null;
		m_except = e;
		if (e != null && !(e instanceof WrapperRuntimeException))
			initErrorMessageService();
	}

	private void initErrorMessageService()
	{
		if (s_inCallStack.get() == null)  {

		s_inCallStack.set(this);
		Trace.println(getMessage());
		Trace.error(com.gihow.dfc.Trace.class, getMessage(), this);
		ErrorMessageService.getService().setThrowable(this);
		s_inCallStack.set(null);

		s_inCallStack.set(null);
        }
	}

	public Throwable fillInStackTrace()
	{
		return m_except != null ? m_except.fillInStackTrace() : super.fillInStackTrace();
	}

	public String getLocalizedMessage()
	{
		return m_except != null ? m_except.getLocalizedMessage() : super.getLocalizedMessage();
	}

	public String getMessage()
	{
		String strMessage = super.getMessage();
		StringBuffer buf = new StringBuffer(1024);
		String message = m_except == null ? null : m_except.getMessage();
		if (strMessage == null || strMessage.length() == 0)
		{
			if (m_except != null && message != null)
				buf.append(message);
		} else
		{
			buf.append(strMessage);
			if (m_except != null && message != null && !buf.toString().endsWith(message))
				buf.append(" : ").append(message);
		}
		return buf.toString();
	}

	public void printStackTrace()
	{
		if (m_except == null)
			super.printStackTrace();
		else
			printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream s)
	{
		if (m_except == null)
			super.printStackTrace(s);
		else
			s.print(ExceptionUtils.getFullStackTrace(m_except));
	}

	public void printStackTrace(PrintWriter s)
	{
		if (m_except == null)
			super.printStackTrace(s);
		else
			s.print(ExceptionUtils.getFullStackTrace(m_except));
	}

	public String toString()
	{
		return m_except != null ? m_except.toString() : super.toString();
	}

	public StackTraceElement[] getStackTrace()
	{
		return m_except != null ? m_except.getStackTrace() : super.getStackTrace();
	}

	public Throwable getException()
	{
		return m_except;
	}

	public String getDetails()
	{
		return m_strDetails;
	}

}
