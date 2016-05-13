// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HttpConfigReaderFactory.java

package com.gihow.dfc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

// Referenced classes of package com.documentum.web.formext.config:
//			IConfigReader

public class HttpConfigReaderFactory
{

	public HttpConfigReaderFactory()
	{
	}

	public static synchronized IConfigReader getConfigReader(ServletContext servletContext, HttpServletRequest request)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("com.documentum.web.formext.Environment", LocaleService.getLocale());
		if (bundle == null)
			throw new WrapperRuntimeException("HttpConfigReaderFactory: Cannot locate com.documentum.web.formext.Environment.properties");
		String strConfigReaderClass = bundle.getString("ConfigReaderClass");
		if (strConfigReaderClass == null || strConfigReaderClass.length() == 0)
			throw new WrapperRuntimeException("HttpConfigReaderFactory: ConfigReaderClass entry missing from com.documentum.web.formext.Environment.properties");
		IConfigReader configReader;
		try
		{
			Class configReaderClass = Class.forName(strConfigReaderClass);
			Constructor constructor = configReaderClass.getConstructor(new Class[] {
				javax.servlet.ServletContext.class, javax.servlet.http.HttpServletRequest.class
			});
			configReader = (IConfigReader)constructor.newInstance(new Object[] {
				servletContext, request
			});
			if (Trace.CONFIGSERVICE)
				Trace.println((new StringBuilder()).append("Config Reader class created: ").append(strConfigReaderClass).toString());
		}
		catch (ClassNotFoundException e)
		{
			throw new WrapperRuntimeException((new StringBuilder()).append("Config Reader class not found: ").append(strConfigReaderClass).toString(), e);
		}
		catch (IllegalAccessException e)
		{
			throw new WrapperRuntimeException((new StringBuilder()).append("Unable to access Config Reader class: ").append(strConfigReaderClass).toString(), e);
		}
		catch (InstantiationException e)
		{
			throw new WrapperRuntimeException((new StringBuilder()).append("Unable to instantiate Config Reader class: ").append(strConfigReaderClass).toString(), e);
		}
		catch (NoSuchMethodException e)
		{
			throw new WrapperRuntimeException((new StringBuilder()).append("Config Reader class does not implement IConfigReader interface: ").append(strConfigReaderClass).toString(), e);
		}
		catch (InvocationTargetException e)
		{
			throw new WrapperRuntimeException((new StringBuilder()).append("An error occurred while initializing config reader class: ").append(strConfigReaderClass).toString(), e);
		}
		return configReader;
	}
}
