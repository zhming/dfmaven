// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Cache.java

package com.gihow.dfc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.Principal;
import java.util.*;

public class Cache
{
	private static class ClearCacheMonitor extends Thread
	{

		public void run()
		{
            try
            {
                sleep(1000 * Cache.s_nClearCacheFlagDuration);
            }
            catch (InterruptedException e)
            {
                synchronized ("CacheLock")
                {
                    Cache.s_fClearCacheFlag = false;
                }
            }
            finally
            {
                synchronized ("CacheLock")
                {
                    Cache.s_fClearCacheFlag = false;
                }
            }
            synchronized ("CacheLock")
            {
                Cache.s_fClearCacheFlag = false;
            }
		}

		public ClearCacheMonitor()
		{
		}
	}


	private static ResourceBundle s_bundle = null;
	private int s_iCacheTime;
	private static String s_strStore = null;
	private static boolean bCacheInSession = true;
	private boolean s_bEnabled;
	private static String BUNDLE_CACHE_DIRECTORY = "CacheDirectory";
	private static String BUNDLE_CACHE_ENABLED = "CachingEnabled";
	private static String BUNDLE_CACHE_USING_SESSIONS = "CacheInSession";
	private static String BUNDLE_CACHE_TIME = "CacheTime";
	private static String BUNDLE_CACHE_INVALDATE = "CacheInvalidation";
	public static final String PRINCIPAL = "Cache.Principal";
	public static final String CACHE = "COMPONENTCACHE";
	public static final String ID = "id";
	public static final String ACTIVEID = "activeid";
	public static final String CACHEBAG = "Cache";
	public static final String CACHELOCK = "CacheLock";
	public static final int DEFAULT_REFRESH = 30;
	private static boolean s_fClearCacheFlag = false;
	private static int s_nClearCacheFlagDuration = 30;
	private static Cache s_cache = null;

	private Cache()
	{
		s_iCacheTime = 30;
		s_bEnabled = false;
		readConfiguration();
	}

	public static Cache getCache()
	{
		if (s_cache == null)
			s_cache = new Cache();
		return s_cache;
	}

	public String getPage(HttpServletRequest request)
	{
		if (s_fClearCacheFlag)
			request.getSession().removeAttribute("Cache");
		String strPage = null;
		String strId = getCacheId(request);
		String strActiveId = null;
		strActiveId = (String)request.getSession().getAttribute("activeid");
		if (strActiveId == null)
		{
			strActiveId = request.getParameter("activeid");
			if (strActiveId == null)
				strActiveId = (String)request.getAttribute("activeid");
		}
		if (s_bEnabled && strId != null)
		{
			if (strActiveId == null || !strId.equals(strActiveId))
			{
				long lPageCacheTime = 1000 * s_iCacheTime;
				long lCurrentTime = System.currentTimeMillis();
				HttpSession httpsession = request.getSession();
				if (bCacheInSession && httpsession != null)
				{
					Hashtable hashtable1 = (Hashtable)httpsession.getAttribute("Cache");
					Vector vector1;
					if (hashtable1 != null && (vector1 = (Vector)hashtable1.get(strId)) != null)
					{
						strPage = (String)vector1.elementAt(0);
						Long lCachedTime = (Long)vector1.elementAt(1);
						if (lCurrentTime - lCachedTime.longValue() > lPageCacheTime)
							strPage = null;
					}
				} else
				{
					if (Trace.CACHE)
						Trace.println("Checking for cached file:");
					if (s_strStore != null)
					{
						String strFilename = (new StringBuilder()).append(getFileDirWithSep()).append(strId).append(httpsession.getId()).toString();
						strPage = checkFile(strFilename, lCurrentTime, lPageCacheTime);
					}
				}
			}
			if (Trace.CACHE)
				if (strPage == null)
					Trace.println((new StringBuilder()).append("Page with Id ").append(strId).append(" not retrieved from cache").toString());
				else
					Trace.println((new StringBuilder()).append("Page with Id ").append(strId).append(" retrieved from cache").toString());
		}
		return strPage;
	}

	public boolean addPage(HttpServletRequest request, String strPage)
	{
		if (queryCache(strPage) && s_bEnabled)
		{
			String strId = getCacheId(request);
			if (strId != null)
			{
				Vector vector = new Vector();
				HttpSession httpsession = null;
				vector.addElement(strPage);
				vector.addElement(Long.valueOf((new Date()).getTime()));
				httpsession = request.getSession();
				if (bCacheInSession)
				{
					if (httpsession != null)
					{
						Hashtable hashtable1 = (Hashtable)httpsession.getAttribute("Cache");
						if (hashtable1 == null)
						{
							hashtable1 = new Hashtable();
							httpsession.setAttribute("Cache", hashtable1);
						}
						hashtable1.put(strId, vector);
						if (Trace.CACHE)
							Trace.println((new StringBuilder()).append("Added Page to session cache with Id ").append(strId).toString());
					}
				} else
				if (s_strStore != null)
				{
					String strFilename = (new StringBuilder()).append(getFileDirWithSep()).append(strId).toString();
					if (httpsession != null)
						strFilename = (new StringBuilder()).append(strFilename).append(httpsession.getId()).toString();
					try
					{
						FileWriter filewriter = new FileWriter(strFilename);
						filewriter.write(strPage);
						filewriter.close();
						if (Trace.CACHE)
							Trace.println((new StringBuilder()).append("Added Page to file cache with Id ").append(strId).toString());
					}
					catch (IOException _ex) { }
				}
			} else
			if (Trace.CACHE)
				Trace.println("Page not added to cache");
		}
		HttpServletRequest httpRequest = (HttpServletRequest)request.getAttribute("javax.servlet.request");
		if (httpRequest != null)
		{
			Principal principal = httpRequest.getUserPrincipal();
			request.getSession().setAttribute("Cache.Principal", principal == null ? null : ((Object) (principal.getName())));
		}
		return true;
	}

	private String getFileDirWithSep()
	{
		String strFile = s_strStore;
//		String s = System.getProperty("file.separator");
//		s = SECURITY.validator().getValidatedPath("FileSeparator", s, 2, null);
//		if (!s_strStore.endsWith(s))
//			strFile = (new StringBuilder()).append(s_strStore).append(s).toString();
		return strFile;
	}

	public void clearCache(HttpServletRequest request, String strId)
	{
		String s1 = null;
		HttpSession httpsession = null;
		httpsession = request.getSession();
		if (httpsession != null)
		{
			Hashtable hashtable1 = (Hashtable)httpsession.getAttribute("Cache");
			if (hashtable1 != null)
				hashtable1.remove(strId);
			if (s_strStore != null)
				s1 = (new StringBuilder()).append(getFileDirWithSep()).append(strId).append(httpsession.getId()).toString();
		}
		if (s1 != null && httpsession != null)
		{
			File file = new File(s1);
			file.delete();
		}
	}

	public static void invalidate(HttpSession session)
	{
		session.removeAttribute("Cache");
		synchronized ("CacheLock")
		{
			if (!s_fClearCacheFlag)
			{
				s_fClearCacheFlag = true;
				ClearCacheMonitor monitor = new ClearCacheMonitor();
				monitor.setDaemon(true);
				monitor.start();
			}
		}
	}

	private String readFile(String strFilename)
	{
		StringBuffer stringbuffer = new StringBuffer("");
		try
		{
			BufferedReader bufferedreader = new BufferedReader(new FileReader(strFilename));
			String s1;
			while ((s1 = bufferedreader.readLine()) != null) 
			{
				stringbuffer.append(s1);
				stringbuffer.append("\n");
			}
			bufferedreader.close();
		}
		catch (FileNotFoundException _ex) { }
		catch (IOException _ex) { }
		return stringbuffer.toString();
	}

	private String checkFile(String strFilename, long lCurrentTime, long lPageCacheTime)
	{
		File file = new File(strFilename);
		if (file.isFile() && file.canRead())
		{
			if (lCurrentTime - file.lastModified() > lPageCacheTime)
				return null;
			else
				return readFile(strFilename);
		} else
		{
			return null;
		}
	}

	private void readConfiguration()
	{
		if (s_bundle == null)
			try
			{
				s_bundle = ResourceBundle.getBundle(getClass().getName());
			}
			catch (MissingResourceException e) { }
		if (s_bundle != null)
		{
			try
			{
				s_strStore = s_bundle.getString(BUNDLE_CACHE_DIRECTORY);
			}
			catch (NumberFormatException e) { }
			catch (MissingResourceException mse) { }
			try
			{
				s_bEnabled = Boolean.valueOf(s_bundle.getString(BUNDLE_CACHE_ENABLED)).booleanValue();
			}
			catch (NumberFormatException e) { }
			catch (MissingResourceException mse) { }
			try
			{
				bCacheInSession = Boolean.valueOf(s_bundle.getString(BUNDLE_CACHE_USING_SESSIONS)).booleanValue();
			}
			catch (NumberFormatException e) { }
			catch (MissingResourceException mse) { }
			try
			{
				s_iCacheTime = (new Integer(s_bundle.getString(BUNDLE_CACHE_TIME))).intValue();
			}
			catch (NumberFormatException e) { }
			catch (MissingResourceException mse) { }
			try
			{
				s_nClearCacheFlagDuration = (new Integer(s_bundle.getString(BUNDLE_CACHE_INVALDATE))).intValue();
			}
			catch (NumberFormatException e) { }
			catch (MissingResourceException mse) { }
		}
	}

	private boolean queryCache(String strPage)
	{
		boolean fCache = true;
		if (strPage.indexOf("_messageservice") > 0)
			fCache = false;
		return fCache;
	}

	public boolean isActiveComponent(HttpServletRequest req)
	{
		boolean fIsActive = false;
		String strId = getCacheId(req);
		String strActiveId = null;
		strActiveId = (String)req.getSession().getAttribute("activeid");
		if (strActiveId == null)
		{
			strActiveId = req.getParameter("activeid");
			if (strActiveId == null)
				strActiveId = (String)req.getAttribute("activeid");
		}
		if (strActiveId != null && strId != null && strId.equals(strActiveId))
			fIsActive = true;
		if (strActiveId == null)
			fIsActive = true;
		return fIsActive;
	}

	public static String getCacheId(HttpServletRequest request)
	{

//		String strId = SECURITY.validator().getValidParameterValue("id", request.getParameter("id"), "XSS");
//		if (strId == null)
//			strId = SECURITY.validator().getValidParameterValue("id", (String)request.getAttribute("id"), "XSS");
//		HttpServletRequest httpRequest = (HttpServletRequest)request.getAttribute("javax.servlet.request");
//		if (httpRequest != null)
//		{
//			Principal p = httpRequest.getUserPrincipal();
//			String strCurrentPrincipalName = p != null ? p.getName() : null;
//			String strLastPrincipal = (String)request.getSession().getAttribute("Cache.Principal");
//			if (strLastPrincipal == null && strCurrentPrincipalName != null || strLastPrincipal != null && strCurrentPrincipalName == null || strCurrentPrincipalName != null && !strCurrentPrincipalName.equalsIgnoreCase(strLastPrincipal))
//				strId = null;
//		}
//		return strId;
        return null;
	}



}
