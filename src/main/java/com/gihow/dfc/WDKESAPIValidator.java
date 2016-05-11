// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   WDKESAPIValidator.java

package com.gihow.dfc;

import com.documentum.fc.common.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.servlet.http.Cookie;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;
import org.owasp.esapi.errors.ValidationException;

public class WDKESAPIValidator
	implements IValidator
{

	public WDKESAPIValidator()
	{
	}

	public String getValidParameterValue(String context, String input, String type)
	{
		if (input == null)
			return null;
		if (input.length() == 0)
			return "";
		String clean = null;
		try
		{
			try
			{
				clean = ESAPI.validator().getValidInput((new StringBuilder()).append("HTTP Parameter Value: ").append(type).append(":").append(context).toString(), new String(input.getBytes("UTF-8"), "UTF-8"), "HTTPParameterValue", 4096, true);
			}
			catch (UnsupportedEncodingException e)
			{
				throw new RuntimeException(e.getMessage());
			}
		}
		catch (ValidationException e)
		{
			throw new RuntimeException(e.getMessage());
		}
		return clean;
	}

	public Cookie[] getValidCookies(String context, Cookie cookies[], String type)
	{
		if (cookies == null)
			return null;
		List newCookies = new ArrayList();
		Cookie arr$[] = cookies;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			Cookie c = arr$[i$];
			try
			{
				String name = ESAPI.validator().getValidInput((new StringBuilder()).append("Cookie name: ").append(type).append(":").append(context).append(": ").append(c.getName()).toString(), c.getName(), "HTTPCookieName", 300, true);
				String value = ESAPI.validator().getValidInput((new StringBuilder()).append("Cookie value: ").append(type).append(":").append(context).append(": ").append(c.getValue()).toString(), c.getValue(), "HTTPCookieValue", 4096, true);
				int maxAge = c.getMaxAge();
				String domain = c.getDomain();
				String path = c.getPath();
				Cookie n = new Cookie(name, value);
				n.setMaxAge(maxAge);
				if (domain != null)
					n.setDomain(ESAPI.validator().getValidInput((new StringBuilder()).append("Cookie domain: ").append(type).append(":").append(context).append(": ").append(domain).toString(), domain, "HTTPHeaderValue", 200, false));
				if (path != null)
					n.setPath(ESAPI.validator().getValidInput((new StringBuilder()).append("Cookie path: ").append(type).append(":").append(context).append(": ").append(path).toString(), path, "HTTPHeaderValue", 200, false));
				newCookies.add(n);
			}
			catch (ValidationException e)
			{
				throw new RuntimeException(e.getMessage());
			}
		}

		return (Cookie[])newCookies.toArray(new Cookie[newCookies.size()]);
	}

	public Map getValidParameterMap(String context, Map map, String type)
	{
		Map cleanMap = new HashMap();
		for (Iterator i$ = map.entrySet().iterator(); i$.hasNext();)
		{
			Object o = i$.next();
			try
			{
				Map.Entry e = (Map.Entry)o;
				String name = (String)e.getKey();
				String cleanName = ESAPI.validator().getValidInput((new StringBuilder()).append("HTTP parameter name: ").append(type).append(":").append(context).append(": ").append(name).toString(), name, "HTTPParameterName", 500, true);
				String value[] = (String[])(String[])e.getValue();
				String cleanValues[] = new String[value.length];
				int j = 0;
				do
				{
					if (j >= value.length)
						break;
					if (value[j] == null)
					{
						cleanValues[j] = null;
						break;
					}
					if (value[j].length() == 0)
					{
						cleanValues[j] = "";
						break;
					}
					String cleanValue = ESAPI.validator().getValidInput((new StringBuilder()).append("HTTP parameter value: ").append(type).append(":").append(context).append(": ").append(value[j]).toString(), new String(value[j].getBytes("UTF-8"), "UTF-8"), "HTTPParameterValue", 4096, true);
					cleanValues[j] = cleanValue;
					j++;
				} while (true);
				cleanMap.put(cleanName, cleanValues);
			}
			catch (ValidationException e)
			{
				throw new RuntimeException(e.getMessage());
			}
			catch (UnsupportedEncodingException e)
			{
				throw new RuntimeException(e.getMessage());
			}
		}

		return cleanMap;
	}

	public Enumeration getValidParameterNames(String context, Enumeration en, String type)
	{
		Vector v = new Vector();
		while (en.hasMoreElements()) 
			try
			{
				String name = (String)en.nextElement();
				String clean = ESAPI.validator().getValidInput((new StringBuilder()).append("HTTP parameter name: ").append(type).append(":").append(context).append(": ").append(name).toString(), name, "HTTPParameterName", 150, true);
				v.add(clean);
			}
			catch (ValidationException e)
			{
				throw new RuntimeException(e.getMessage());
			}
		return v.elements();
	}

	public String[] getValidParameterValues(String context, String values[], String type)
	{
		List newValues = new ArrayList();
		String arr$[];
		return (String[])newValues.toArray(new String[newValues.size()]);
	}

	public String getValidQueryString(String context, String query, String type)
	{
		if (query == null)
			return null;
		if (query.length() == 0)
			return "";
		String clean = "";
		try
		{
			clean = ESAPI.validator().getValidInput((new StringBuilder()).append("HTTP query string: ").append(type).append(":").append(context).append(": ").append(query).toString(), query, "HTTPQueryString", 2000, true);
		}
		catch (ValidationException e)
		{
			throw new RuntimeException(e.getMessage());
		}
		return clean;
	}

	public String getValidPathInfo(String context, String path, String type)
	{
		if (path == null)
			return null;
		if (path.length() == 0)
			return "";
		String clean = "";
		try
		{
			clean = ESAPI.validator().getValidInput((new StringBuilder()).append("HTTP path: ").append(type).append(":").append(context).append(": ").append(path).toString(), path, "HTTPPath", 150, true);
		}
		catch (ValidationException e)
		{
			throw new RuntimeException(e.getMessage());
		}
		return clean;
	}

	public String getValidServletPath(String context, String path, String type)
	{
		if (path == null)
			return null;
		if (path.length() == 0)
			return "";
		String clean = "";
		try
		{
			clean = ESAPI.validator().getValidInput((new StringBuilder()).append("HTTP servlet path: ").append(type).append(":").append(context).append(": ").append(path).toString(), path, "HTTPServletPath", 100, false);
		}
		catch (ValidationException e)
		{
			throw new RuntimeException(e.getMessage());
		}
		return clean;
	}

	public String getValidURL(String context, String url, String type)
	{
		if (url == null)
			return null;
		if (url.length() == 0)
			return "";
		int index = url.indexOf("?");
		String protocolHost = "";
		String uriPath;
		String args;
		if (index != -1)
		{
			uriPath = url.substring(0, index);
			args = url.substring(index, url.length());
		} else
		{
			uriPath = url;
			args = "";
		}
		int pIndex = uriPath.indexOf("http");
		String uriPathSplit;
		if (pIndex == 0)
		{
			int hIndex = uriPath.indexOf("//");
			protocolHost = (new StringBuilder()).append(uriPath.substring(0, hIndex)).append("//").toString();
			uriPathSplit = uriPath.substring(protocolHost.length());
			int rIndex = uriPathSplit.indexOf("/");
			protocolHost = (new StringBuilder()).append(protocolHost).append(uriPathSplit.substring(0, rIndex)).toString();
			if (rIndex != -1)
				uriPathSplit = uriPathSplit.substring(rIndex);
		} else
		{
			uriPathSplit = uriPath;
		}
		String clean = "";
		try
		{
			clean = ESAPI.validator().getValidInput((new StringBuilder()).append("HTTP URL: ").append(type).append(":").append(context).append(": ").append(uriPathSplit).toString(), uriPathSplit, "HTTPURI", 2048, false);
		}
		catch (ValidationException e)
		{
			throw new RuntimeException(e.getMessage());
		}
		if (!clean.equals(""))
			return (new StringBuilder()).append(protocolHost).append(clean).append(args).toString();
		else
			return clean;
	}

	public String getValidHeader(String context, String headerValue, String type)
	{
		if (headerValue == null)
			return null;
		if (headerValue.length() == 0)
			return "";
		String clean = "";
		try
		{
			clean = ESAPI.validator().getValidInput((new StringBuilder()).append("HTTP header value: ").append(type).append(": ").append(context).toString(), headerValue, "HTTPHeaderValue", 1024, true);
		}
		catch (ValidationException e)
		{
			throw new RuntimeException(e.getMessage());
		}
		return clean;
	}

	public String getValidatedPath(String context, String pathValue, int pathType, Map values)
	{
		if (pathValue == null)
			return null;
		switch (pathType)
		{
		case 0: // '\0'
			IDfId dfId = new DfId(pathValue);
			if (dfId.isObjectId())
				return pathValue;
			else
				throw new RuntimeException("Invalid Object Id");

		case 1: // '\001'
			String repoNameRule1 = "Repository name only can contain characters: A-Z, a-z, 0-9, underscore(_)";
			String repoNameRule2 = "The length of repository name must be less than 32 characters.";
			String repoNameRule3 = "First character of repository name must be a letter.";
			String repoNameRule4 = "Repository name can not be empty.";
			String goodList = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
			if (pathValue.length() == 0)
				throw new RuntimeException(repoNameRule4);
			if (pathValue.length() > 32)
				throw new RuntimeException(repoNameRule2);
			char tempChars[] = pathValue.toCharArray();
			if (!Character.isLetter(tempChars[0]))
				throw new RuntimeException(repoNameRule3);
			for (int i = 0; i < tempChars.length; i++)
				if (goodList.indexOf(tempChars[i]) == -1)
					throw new RuntimeException(repoNameRule1);

			return pathValue;

		case 2: // '\002'
			if (pathValue.equals("/") || pathValue.equals("\\"))
				return pathValue;
			else
				throw new RuntimeException("Invalid file separator");

		case 3: // '\003'
			String localFileDir = pathValue.substring(0, pathValue.lastIndexOf(File.separator));
			String export = localFileDir.substring(localFileDir.lastIndexOf(File.separator) + File.separator.length(), localFileDir.length());
			String exportDirectory = DfPreferences.getInstance().getExportDirectory();
			if (pathValue.contains(exportDirectory) && "export".equalsIgnoreCase(export))
				return pathValue;
			else
				throw new RuntimeException("Invalid filepath");

		case 4: // '\004'
			if (pathValue.equals("http") || pathValue.equals("https"))
				return pathValue;
			else
				throw new RuntimeException("Invalid protocol");
		}
		return null;
	}

    @Override
    public boolean getIsValid() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void validate() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getErrorMessage() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
