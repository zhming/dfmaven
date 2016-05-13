// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HttpConfigReader.java

package com.gihow.dfc;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

// Referenced classes of package com.documentum.web.formext.config:
//			ConfigFile, IConfigReader

public class HttpConfigReader
	implements IConfigReader
{

	private String m_strRootFolderPath;
	private String m_strPrimaryAppFolderName;
	private static final String APP_FOLDER_NAME = "AppFolderName";

	public HttpConfigReader(ServletContext servletContext, HttpServletRequest req)
	{
		m_strRootFolderPath = servletContext.getRealPath("/dfmaven/web/WEB-INF/classes");
		if (m_strRootFolderPath != null)
		{
			int iPos = m_strRootFolderPath.length() - "wdk".length();
			for (char chSep = File.separator.toCharArray()[0]; m_strRootFolderPath.charAt(iPos - 1) == chSep; iPos--);
			m_strRootFolderPath = m_strRootFolderPath.substring(0, iPos);
		} else
		{
			m_strRootFolderPath = servletContext.getRealPath("/");
		}
		if (m_strRootFolderPath == null)
			m_strRootFolderPath = "";
		m_strPrimaryAppFolderName = servletContext.getInitParameter("AppFolderName");
		ResourceFileUtil.setLookupContext(servletContext, null);
	}

	public String getAppName()
	{
		return m_strPrimaryAppFolderName;
	}

	public String getRootFolderPath()
	{
		return m_strRootFolderPath;
	}

	public ConfigFile loadAppConfigFile(String strAppName)
	{
		String strPath = concatPath(concatPath(m_strRootFolderPath, strAppName), "app.xml");
		return new ConfigFile(strPath, null);
	}

	public Iterator loadConfigFiles(String strAppName)
	{
		ArrayList configFiles = new ArrayList(1000);
		String strFolderPath = concatPath(concatPath(m_strRootFolderPath, strAppName), "config");
		loadConfigFiles(configFiles, strFolderPath, null);
		return configFiles.iterator();
	}

	private void loadConfigFiles(ArrayList configFiles, String strFolderPath, String strAppName)
	{
		File folder = new File(strFolderPath);
		if (!folder.exists())
			folder = new File((new StringBuilder()).append(File.separator).append(strFolderPath).toString());
		if (folder.exists())
		{
			String strFiles[] = folder.list();
			for (int iFile = 0; iFile < strFiles.length; iFile++)
			{
				String strFileName = strFiles[iFile];
				String strFilePathName = concatPath(strFolderPath, strFileName);
				File file = new File(strFilePathName);
				if (file.isFile() && strFileName.endsWith(".xml"))
				{
					ConfigFile configFile = new ConfigFile(strFilePathName, strAppName);
					configFiles.add(configFile);
				} else
				if (file.isDirectory())
					loadConfigFiles(configFiles, strFilePathName, strAppName);
			}

		} else
		{
			ConfigFile configFile;
			for (Iterator iterSubs = ResourceFileUtil.findAllFilesInWebApp(strFolderPath, "xml"); iterSubs.hasNext(); configFiles.add(configFile))
			{
				String strFilePathName = (String)iterSubs.next();
				configFile = new ConfigFile(strFilePathName, strAppName);
			}

		}
	}

	private String concatPath(String strPath1, String strPath2)
	{
		String strResult = strPath2;
		if (strPath1.length() > 0)
		{
			if (strPath1.endsWith("/") || strPath1.endsWith("\\"))
				strPath1 = strPath1.substring(0, strPath1.length() - 1);
			if (strPath2.startsWith("/") || strPath2.startsWith("\\"))
				strPath2 = strPath2.substring(1);
			strResult = (new StringBuilder()).append(strPath1).append("/").append(strPath2).toString();
		}
		return strResult;
	}
}
