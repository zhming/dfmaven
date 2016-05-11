// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   AbstractEnvironment.java

package com.gihow.dfc;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

// Referenced classes of package com.documentum.web.env:
//			ITag, ISession, ISuperCredentials, IPreference, 
//			IDispatch, ILogin, IRender, ILocale, 
//			ISecurity, IError, IContentTransfer, ITheme, 
//			IActionPrecondition, IMessageService, IFormOperationListener, IInfo

public abstract class AbstractEnvironment
	implements IParams, Serializable
{

	private static final String ATTR_REQUEST = "request";
	private static final String ATTR_RESPONSE = "response";

	public abstract IInfo getEnvironmentInfo();

	public String toString()
	{
		IInfo envInfo = getEnvironmentInfo();
		StringBuffer buf = new StringBuffer(256);
		buf.append((new StringBuilder()).append("[Name = ").append(envInfo.getEnvironmentName()).toString());
		buf.append((new StringBuilder()).append(", Description = ").append(envInfo.getEnvironmentDescription()).toString());
		buf.append((new StringBuilder()).append(", Major Version = ").append(envInfo.getEnvironmentMajorVersion()).toString());
		buf.append((new StringBuilder()).append(", Minor Version = ").append(envInfo.getEnvironmentMinorVersion()).toString());
		buf.append((new StringBuilder()).append(", Author = ").append(envInfo.getEnvironmentAuthor()).toString());
		buf.append((new StringBuilder()).append(", Portal Description = ").append(envInfo.getPortalDescription()).toString());
		buf.append((new StringBuilder()).append(", Portal Version = ").append(envInfo.getPortalVersion()).toString());
		buf.append((new StringBuilder()).append(", Environment Base Version = ").append(envInfo.getEnvironmentBaseVersion()).toString());
		buf.append((new StringBuilder()).append(", Environment Service Version = ").append(envInfo.getEnvironmentServiceVersion()).toString());
		Class oClass = null;
		try
		{
			oClass = Class.forName("com.documentum.web.env.AbstractEnvironment");
		}
		catch (ClassNotFoundException e) { }
		if (oClass != null)
		{
			Method methods[] = oClass.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++)
			{
				String strMethodName = methods[i].getName();
				if (!strMethodName.startsWith("get") || !strMethodName.endsWith("Contract"))
					continue;
				String strName = strMethodName.substring("get".length(), strMethodName.length() - "Contract".length());
				try
				{
					Object oRet = methods[i].invoke(this, null);
					buf.append((new StringBuilder()).append(", Supports ").append(strName).append(" Contract = ").append(oRet != null).toString());
				}
				catch (IllegalAccessException e)
				{
					System.out.println(e);
				}
				catch (InvocationTargetException e)
				{
					System.out.println(e);
				}
			}

		}
		buf.append("]");
		return buf.toString();
	}

	public ITag getTagContract()
	{
		ITag iTag = null;
		if (this instanceof ITag)
			iTag = (ITag)this;
		return iTag;
	}

	public final ISession getSessionContract()
	{
		ISession iContract = null;
		if (this instanceof ISession)
			iContract = (ISession)this;
		return iContract;
	}

	public final ISuperCredentials getSuperCredentialsContract()
	{
		ISuperCredentials iContract = null;
		if (this instanceof ISuperCredentials)
			iContract = (ISuperCredentials)this;
		return iContract;
	}

	public final IPreference getPreferenceContract()
	{
		IPreference iContract = null;
		if (this instanceof IPreference)
			iContract = (IPreference)this;
		return iContract;
	}

	public IDispatch getDispatchContract()
	{
		IDispatch iDispatch = null;
		if (this instanceof IDispatch)
			iDispatch = (IDispatch)this;
		return iDispatch;
	}

	public final ILogin getLoginContract()
	{
		ILogin iLogin = null;
		if (this instanceof ILogin)
			iLogin = (ILogin)this;
		return iLogin;
	}

	public final IRender getRenderContract()
	{
		IRender iContract = null;
		if (this instanceof IRender)
			iContract = (IRender)this;
		return iContract;
	}

	public final ILocale getLocaleContract()
	{
		ILocale iContract = null;
		if (this instanceof ILocale)
			iContract = (ILocale)this;
		return iContract;
	}

	public final ISecurity getSecurityContract()
	{
		ISecurity iContract = null;
		if (this instanceof ISecurity)
			iContract = (ISecurity)this;
		return iContract;
	}

	public final IError getErrorContract()
	{
		IError iError = null;
		if (this instanceof IError)
			iError = (IError)this;
		return iError;
	}

	public final IContentTransfer getContentTransferContract()
	{
		IContentTransfer iContract = null;
		if (this instanceof IContentTransfer)
			iContract = (IContentTransfer)this;
		return iContract;
	}

	public final ITheme getThemeContract()
	{
		ITheme iContract = null;
		if (this instanceof ITheme)
			iContract = (ITheme)this;
		return iContract;
	}

	/**
	 * @deprecated Method getActionPreconditionContract is deprecated
	 */

	public final IActionPrecondition getActionPreconditionContract()
	{
		IActionPrecondition iContract = null;
		if (this instanceof IActionPrecondition)
			iContract = (IActionPrecondition)this;
		return iContract;
	}

	public final IMessageService getMessageServiceContract()
	{
		IMessageService iContract = null;
		if (this instanceof IMessageService)
			iContract = (IMessageService)this;
		return iContract;
	}

	public final IFormOperationListener getFormOperationListenerContract()
	{
		IFormOperationListener iContract = null;
		if (this instanceof IFormOperationListener)
			iContract = (IFormOperationListener)this;
		return iContract;
	}

	public abstract ResourceBundle getResourceBundle();

	public boolean isPortalEnvironment()
	{
		return false;
	}

	final void notifyRequestStart(HttpServletRequest request, HttpServletResponse response)
	{
		setAttribute("request", request);
		setAttribute("response", response);
	}

	final void notifyRequestFinish(HttpServletRequest request, HttpServletResponse response)
	{
		removeAttribute("request");
		removeAttribute("response");
	}

	protected abstract void setAttribute(String s, Object obj);

	protected abstract Object getAttribute(String s);

	protected abstract void removeAttribute(String s);

	protected final HttpSession getSession()
	{
		HttpSession session = null;
		HttpServletRequest req = (HttpServletRequest)getAttribute("request");
		if (req != null)
			session = req.getSession();
		return session;
	}

	protected final HttpServletRequest getRequest()
	{
		return (HttpServletRequest)getAttribute("request");
	}

	protected final HttpServletResponse getResponse()
	{
		return (HttpServletResponse)getAttribute("response");
	}

	protected AbstractEnvironment()
	{
	}
}
