// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ResponseHeaderControlFilter.java

package com.gihow.dfc;


import com.gihow.util.SafeHTMLString;
import com.gihow.util.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Pattern;

// Referenced classes of package com.documentum.web.servlet:
//			Trace

public class ResponseHeaderControlFilter
	implements Filter
{

	private FilterConfig m_config;
	private boolean m_bLogStats;
	private String m_headers[][];
	private static final int HEADER_NAME = 0;
	private static final int HEADER_VALUE = 1;
	private static final String X_FRAME_OPTIONS = "X-FRAME-OPTIONS";
	private static final String x_frame_option = "application.security_support.x_frame_option.enabled";
	private boolean frame_option;
	private String x_frame_option_value;
	private static final String s_prefix = "/_0/";
	private static final String STR_INCLUDE_PAGES = "wdk_cache_control_includepages";
	private static Pattern s_patternIncludePages = null;
	private static final String STR_REDIRECT_PAGES = "wdk_cache_control_redirect_includepages";
	private static Pattern s_patternRedirectPages = null;
	private static String STR_PREFIX_SEPERATOR = "cache_prefix_separator";
	private static String STR_PREFIX_SEPERATOR_VALUE = null;

	public ResponseHeaderControlFilter()
	{
		x_frame_option_value = null;
	}

	public static String makeUrl(String strPath, ServletRequest request, boolean fAddContextPath)
	{
		if (request != null && strPath != null && strPath.length() > 0)
		{
			boolean fDoRedirect = isURIForRedirect(strPath);
			if (fDoRedirect)
			{
				String strContextPath = ((HttpServletRequest)request).getContextPath();
				String prefix = getUniquePrefix(strPath, strContextPath);
				if (prefix != null)
				{
					StringBuilder strbuf = new StringBuilder(128);
					strbuf.append(strContextPath);
					if (!fAddContextPath && strPath.startsWith(strContextPath))
					{
						strPath = strPath.substring(strContextPath.length());
						if (!strPath.startsWith("/_0/"))
							strbuf.append(prefix);
					} else
					if (!strPath.startsWith("/_0/"))
						strbuf.append(prefix);
					else
					if (strPath.charAt(0) != '/')
						strbuf.append('/');
					strbuf.append(strPath);
					return strbuf.toString();
				}
			}
			if (fAddContextPath)
			{
				String strContextPath = ((HttpServletRequest)request).getContextPath();
				StringBuilder strbuf = new StringBuilder(128);
				strbuf.append(strContextPath);
				if (strPath.charAt(0) != '/')
					strbuf.append('/');
				strbuf.append(strPath);
				return strbuf.toString();
			}
		}
		return strPath;
	}

	private static String getUniquePrefix(String resourcePath, String contextPath)
	{
		URLConnection iconConn;
		URL url = ResourceFileUtil.getResourceURL(resourcePath, contextPath);
		if (url == null)
			return null;
		iconConn = null;
		String s;
		try
		{
			iconConn = url.openConnection();
			long lastModified = iconConn.getLastModified();
			int contentLength = iconConn.getContentLength();
			StringBuilder prefix = new StringBuilder("/_0/".length() + 16);
			prefix.append("/_0/").append(StringUtil.toUnsignedString(lastModified, 5)).append(STR_PREFIX_SEPERATOR_VALUE == null || !STR_PREFIX_SEPERATOR_VALUE.equals("_") ? "-" : STR_PREFIX_SEPERATOR_VALUE).append(StringUtil.toUnsignedString(contentLength, 5));
			s = prefix.toString();
		}
		catch (IOException e)
		{
			throw new WrapperRuntimeException(e);
		}
		finally
		{
			if (iconConn == null){
                System.out.println("iconConn is null!");
            }
		}
		if (iconConn != null)
			try
			{
				iconConn.getInputStream().close();
			}
			catch (IOException ioe) { }
		return s;

	}

	public static String restoreOriginalUrl(String strPath, ServletRequest request)
	{
		if (strPath != null && strPath.length() > 0)
		{
			String strContextPath = null;
			if (request != null)
				strContextPath = ((HttpServletRequest)request).getContextPath();
			return restoreOriginalUrl(strPath, strContextPath);
		} else
		{
			return strPath;
		}
	}

	public static String restoreOriginalUrl(String strPath, String strContextPath)
	{
		if (strPath != null && strPath.length() > 0)
		{
			String prefix = strContextPath == null ? "/_0/" : (new StringBuilder()).append(strContextPath).append("/_0/").toString();
			if (strPath.startsWith(prefix) && strPath.length() > prefix.length())
			{
				int idx = strPath.indexOf('/', prefix.length() + 1);
				if (idx != -1)
				{
					String original = strPath.substring(idx);
					strPath = strContextPath == null ? original : (new StringBuilder()).append(strContextPath).append(original).toString();
					return strPath;
				}
			}
		}
		return strPath;
	}

	public void init(FilterConfig filterConfig)
		throws ServletException
	{
		m_config = filterConfig;
		STR_PREFIX_SEPERATOR_VALUE = SafeHTMLString.escapeAttribute(filterConfig.getInitParameter("cache_prefix_separator"));
		Map notAHeader = new HashMap();
		notAHeader.put("debug", "");
		notAHeader.put("wdk_cache_control_includepages", "");
		notAHeader.put("wdk_cache_control_redirect_includepages", "");
		notAHeader.put(STR_PREFIX_SEPERATOR, "");
		boolean fHasCachingControl = false;
		LinkedList list = new LinkedList();
		try
		{
			IConfigLookup lookup = ConfigService.getConfigLookup();
			frame_option = lookup.lookupBoolean("application.security_support.x_frame_option.enabled", Context.getApplicationContext()).booleanValue();
		}
		catch (Exception e)
		{
			frame_option = false;
			log(e.toString());
		}
		Enumeration e = filterConfig.getInitParameterNames();
		do
		{
			if (!e.hasMoreElements())
				break;
			String header = (String)e.nextElement();
			if (header != null && header.length() > 0 && !notAHeader.containsKey(header))
			{
				String value = filterConfig.getInitParameter(header);
				if (value != null && value.length() > 0)
				{
					String arr[] = new String[2];
					arr[0] = header;
					arr[1] = value;
					if ("X-FRAME-OPTIONS".equalsIgnoreCase(header))
					{
						if (frame_option)
							x_frame_option_value = value;
					} else
					{
						list.add(arr);
					}
					if (header.equalsIgnoreCase("cache-control") || header.equalsIgnoreCase("expires"))
						fHasCachingControl = true;
				}
			}
		} while (true);
		m_headers = new String[list.size()][2];
		list.toArray(m_headers);
		m_bLogStats = Boolean.valueOf(filterConfig.getInitParameter("debug")).booleanValue();
		if (isLogging())
			log((new StringBuilder()).append("read headers: ").append(Arrays.asList(m_headers).toString()).toString());
		String strIncludedPages = filterConfig.getInitParameter("wdk_cache_control_includepages");
		if (strIncludedPages != null)
		{
			strIncludedPages = strIncludedPages.trim();
			if (strIncludedPages.length() > 0)
			{
				s_patternIncludePages = Pattern.compile(strIncludedPages, 2);
				if (s_patternIncludePages != null && fHasCachingControl)
				{
					String strRedirectKey = filterConfig.getInitParameter("wdk_cache_control_redirect_includepages");
					if (strRedirectKey != null && strRedirectKey.length() > 0)
						s_patternRedirectPages = Pattern.compile(strRedirectKey, 2);
				}
			}
		}
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
		throws IOException, ServletException
	{
		if ((res instanceof HttpServletResponse) )      //&& !WDKController.isIncludedRequest(req)
		{
			HttpServletRequest request = (HttpServletRequest)req;
			HttpServletResponse response = (HttpServletResponse)res;
			String strRequestURI = getRequestURI(request);
			if (frame_option)
				response.setHeader("X-FRAME-OPTIONS", x_frame_option_value);
			if (isURIForFilter(strRequestURI))
			{
				for (int i = 0; i < m_headers.length; i++)
					response.setHeader(m_headers[i][0], m_headers[i][1]);

				if (isLogging())
				{
					StringBuffer sb = new StringBuffer(request.getRequestURI());
					if (request.getSession(false) != null)
					{
						sb.append("; session: ");
						sb.append(request.getSession(false).getId());
					}
					log(sb.toString());
				}
			}
			if (strRequestURI.startsWith("/_0/"))
			{
				String newRequestURI = restoreOriginalUrl(strRequestURI, (String)null);
				newRequestURI = URLDecoder.decode(newRequestURI, "UTF-8");
				m_config.getServletContext().getRequestDispatcher(newRequestURI).forward(req, res);
				return;
			}
		}
		filterChain.doFilter(req, res);
	}

	public void destroy()
	{
	}

	private boolean isLogging()
	{
		return m_bLogStats || Trace.RESPONSE_HEADER_CONTROL;
	}

	private void log(String msg)
	{
		msg = (new StringBuilder()).append(m_config.getFilterName()).append(": ").append(msg).toString();
		Trace.println(msg);
		m_config.getServletContext().log(msg);
	}

	private static boolean isURIForFilter(String strPath)
	{
		boolean fForFilter = false;
		if (s_patternIncludePages != null && strPath != null)
		{
			for (; strPath.length() > 1 && strPath.charAt(strPath.length() - 1) == '/'; strPath = strPath.substring(0, strPath.length() - 1));
			fForFilter = s_patternIncludePages.matcher(strPath).find();
		}
		return fForFilter;
	}

	private static boolean isURIForRedirect(String strPath)
	{
		boolean fForRedirect = false;
		if (s_patternRedirectPages != null)
		{
			for (; strPath.length() > 1 && strPath.charAt(strPath.length() - 1) == '/'; strPath = strPath.substring(0, strPath.length() - 1));
			fForRedirect = s_patternRedirectPages.matcher(strPath).find();
		}
		return fForRedirect;
	}

	private static String getRequestURI(HttpServletRequest request)
	{
		String strPath = request.getRequestURI();
		if (strPath != null && strPath.length() > 0)
		{
			String strContextPath = request.getContextPath();
			if (strContextPath != null && strPath.startsWith(strContextPath))
				strPath = strPath.substring(strContextPath.length());
		}
		return strPath;
	}

}
