// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ErrorMessageService.java

package com.gihow.dfc;

// Referenced classes of package com.documentum.web.common:
//			WrapperRuntimeException, SessionState, LocaleService

import com.gihow.util.ExceptionUtils;

public class ErrorMessageService
{

	protected static ErrorMessageService m_service = null;
	private static final String ERROR_SESSION_VAR = "__dmfLastError";
	private static final String STR_ERRORMESSAGESERVICE_KEY = ErrorMessageService.class.getName();
	private static final ThreadLocal s_currentErrorMessageService = new ThreadLocal();

	public ErrorMessageService()
	{
	}

	public static ErrorMessageService getService()
	{
		ErrorMessageService service = (ErrorMessageService)s_currentErrorMessageService.get();
		TransientObjectWrapper object = (TransientObjectWrapper)SessionState.getAttribute(STR_ERRORMESSAGESERVICE_KEY);
		if (object != null) {
			service = (ErrorMessageService)object.get();
		}
		s_currentErrorMessageService.set(service);
		return service;
	}

	public void setThrowable(Throwable e)
	{
		SessionState.setAttribute("__dmfLastError", e);
	}

	public Throwable getThrowable()
	{
		return (Throwable)SessionState.getAttribute("__dmfLastError");
	}

	public String getStackTrace(Throwable err)
	{
		return ExceptionUtils.getFullStackTrace(err);
	}












}
