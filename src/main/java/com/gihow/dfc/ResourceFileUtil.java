// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ResourceFileUtil.java

package com.gihow.dfc;


import javax.servlet.ServletContext;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

// Referenced classes of package com.documentum.web.common:
//			WrapperRuntimeException, Trace

public class ResourceFileUtil
{

	public static boolean TRACEENABLED = false;
	private static Hashtable s_mapUrls = new Hashtable(89, 1.0F);
	private static ArrayList s_listLookupFolders = new ArrayList(10);
	private static ClassLoader s_loader = null;
	private static ServletContext s_servletContext = null;
	private static String s_strRootFolderRealPath = null;
	private static boolean s_bUnExplodedWarDeployment = false;
	private static String s_strVirtualDir = "";

	public ResourceFileUtil()
	{
	}

	public static void setLookupContext(ServletContext servletContext, String strRootVirtualURL)
	{
		s_servletContext = servletContext;
		String strAppName = "";
		if (strRootVirtualURL != null)
		{
			strAppName = strRootVirtualURL;
			if (strAppName.charAt(0) == '/')
				strAppName = strAppName.substring(1);
			if (strAppName.endsWith("/"))
				strAppName = strAppName.substring(0, strAppName.length() - 1);
			s_strVirtualDir = (new StringBuilder()).append("/").append(strAppName).toString();
		}
		s_strRootFolderRealPath = servletContext.getRealPath("/wdk");
		if (s_strRootFolderRealPath != null)
		{
			int iPos = s_strRootFolderRealPath.length() - "wdk".length();
			for (char chSep = File.separator.toCharArray()[0]; s_strRootFolderRealPath.charAt(iPos - 1) == chSep; iPos--);
			s_strRootFolderRealPath = s_strRootFolderRealPath.substring(0, iPos);
		} else
		{
			s_strRootFolderRealPath = servletContext.getRealPath("/");
		}
		if (TRACEENABLED)
			Trace.println((new StringBuilder()).append("Root folder from servletContext.getRealPath returns ").append(s_strRootFolderRealPath != null ? s_strRootFolderRealPath : "null").toString());
		if (s_strRootFolderRealPath == null)
		{
			s_bUnExplodedWarDeployment = true;
			if (TRACEENABLED)
				Trace.println("Resources will be looked in the unexploded war file");
		} else
		{
			s_strRootFolderRealPath = s_strRootFolderRealPath.replace('\\', '/');
			if (s_strRootFolderRealPath.length() > 1 && s_strRootFolderRealPath.endsWith("/"))
				s_strRootFolderRealPath = s_strRootFolderRealPath.substring(0, s_strRootFolderRealPath.length() - 1);
			File file = new File(s_strRootFolderRealPath);
			if (!file.exists())
				s_strRootFolderRealPath = "";
			else
			if (file.isDirectory())
			{
				s_strRootFolderRealPath = file.getAbsolutePath();
			} else
			{
				File parentFile = file.getParentFile();
				if (parentFile == null)
					s_strRootFolderRealPath = "";
				else
					s_strRootFolderRealPath = parentFile.getAbsolutePath();
				s_strRootFolderRealPath = (new StringBuilder()).append(s_strRootFolderRealPath).append(s_strVirtualDir).append("-config").toString();
			}
			if (TRACEENABLED)
				Trace.println((new StringBuilder()).append("Resource will be looked in folder ").append(s_strRootFolderRealPath).toString());
		}
	}

	public static void addLookupFolder(String strFolder, boolean fAddAtHead)
	{
		if (strFolder == null || strFolder.length() < 1)
		{
			strFolder = "";
		} else
		{
			strFolder = strFolder.replace('\\', '/');
			if (strFolder.charAt(0) != '/')
				strFolder = (new StringBuilder()).append("/").append(strFolder).toString();
			if (strFolder.charAt(strFolder.length() - 1) == '/')
				strFolder = strFolder.substring(0, strFolder.length() - 1);
		}
		synchronized (s_listLookupFolders)
		{
			if (s_listLookupFolders.indexOf(strFolder) < 0)
				if (fAddAtHead)
					s_listLookupFolders.add(0, strFolder);
				else
					s_listLookupFolders.add(strFolder);
		}
	}

	public static InputStream getResourceAsStream(String strResourceName, boolean fSearchJavaResource)
		throws IOException
	{
		if (TRACEENABLED)
			Trace.println((new StringBuilder()).append("Begin looking for Resource as stream: ").append(strResourceName).toString());
		strResourceName = ResponseHeaderControlFilter.restoreOriginalUrl(strResourceName, (String)null);
		InputStream instream = null;
		synchronized (s_listLookupFolders)
		{
			int nLookup = s_listLookupFolders.size();
			for (int idxlist = 0; idxlist < nLookup && instream == null; idxlist++)
			{
				String strTryResourcePath = (new StringBuilder()).append((String)s_listLookupFolders.get(idxlist)).append('/').append(strResourceName).toString();
				if (s_bUnExplodedWarDeployment)
				{
					try
					{
						instream = getResourceAsStream(strTryResourcePath, ((String) (null)));
					}
					catch (Throwable e) { }
					continue;
				}
				String strTryFilename = (new StringBuilder()).append(s_strRootFolderRealPath).append(strTryResourcePath).toString();
				if (TRACEENABLED)
					Trace.println((new StringBuilder()).append("search resource in the file system: ").append(strTryFilename).toString());
				File fileTry = new File(strTryFilename);
				if (fileTry.exists() && fileTry.isFile())
				{
					instream = new BufferedInputStream(new FileInputStream(fileTry));
					if (TRACEENABLED)
						Trace.println((new StringBuilder()).append("found resource in the file system: ").append(strTryFilename).toString());
				}
				fileTry = null;
			}

		}
		if (instream == null && fSearchJavaResource)
		{
			if (TRACEENABLED)
				Trace.println((new StringBuilder()).append("search resource in the java path:").append(strResourceName).toString());
			instream = getResourceClassLoader().getResourceAsStream(strResourceName);
			if (TRACEENABLED && instream != null)
				Trace.println((new StringBuilder()).append("found resource in the java path:").append(strResourceName).toString());
		}
		if (TRACEENABLED)
			Trace.println((new StringBuilder()).append("end looking for Resource as stream: ").append(strResourceName).toString());
		return instream;
	}

	public static InputStream getPropertiesAsStream(String strPropClassName)
		throws IOException
	{
		if (TRACEENABLED)
			Trace.println((new StringBuilder()).append("Begin looking for property as stream: ").append(strPropClassName).toString());
		String strSlashedPropName = strPropClassName.replace('.', '/');
		strSlashedPropName = strSlashedPropName.replace('\\', '/');
		InputStream instream = getResourceAsStream((new StringBuilder()).append("strings/").append(strSlashedPropName).append(".properties").toString(), false);
		if (instream == null)
		{
			if (TRACEENABLED)
				Trace.println((new StringBuilder()).append("Searching java resoure: ").append(strSlashedPropName).append(".properties").toString());
			instream = getResourceClassLoader().getResourceAsStream((new StringBuilder()).append(strSlashedPropName).append(".properties").toString());
			if (TRACEENABLED)
				Trace.println((new StringBuilder()).append("found java resoure: ").append(strSlashedPropName).append(".properties").toString());
		}
		if (TRACEENABLED)
			Trace.println((new StringBuilder()).append("End looking for property as stream: ").append(strPropClassName).toString());
		return instream;
	}

	public static Properties getProperties(String strPropClassName)
		throws IOException
	{
		Properties prop = new Properties();
		InputStream instream = getPropertiesAsStream(strPropClassName);
		if (instream != null)
		{
			prop.load(instream);
			instream.close();
		}
		return prop;
	}

	private static synchronized ClassLoader getResourceClassLoader()
	{
		if (s_loader == null)
		{
			ResourceBundle dummybundle = ResourceBundle.getBundle("com.documentum.nls.ClassLoaderFinderBundle");
			ClassLoader loader = dummybundle.getClass().getClassLoader();
			if (loader == null)
			{
				if (TRACEENABLED)
					Trace.println("using system class loader");
				loader = ClassLoader.getSystemClassLoader();
			}
			if (TRACEENABLED)
				Trace.println((new StringBuilder()).append("using class loader for java resource: ").append(loader.toString()).toString());
			s_loader = loader;
		}
		return s_loader;
	}

	public static Iterator findAllFiles(String strPath, String strExtn, boolean bBaseFirst)
	{
		List lisFiles = new ArrayList();
		int nCount = s_listLookupFolders.size();
		for (int nIdx = 0; nIdx < nCount; nIdx++)
		{
			String strResourcePath = null;
			if (bBaseFirst)
				strResourcePath = (String)s_listLookupFolders.get(nCount - nIdx - 1);
			else
				strResourcePath = (String)s_listLookupFolders.get(nIdx);
			if (s_bUnExplodedWarDeployment)
			{
				findAllFiles((new StringBuilder()).append(strResourcePath).append("/").append(strPath).toString(), lisFiles, strExtn);
				continue;
			}
			File folder = new File(s_strRootFolderRealPath, strResourcePath);
			if (!folder.exists())
				continue;
			if (strPath != null)
				folder = new File(folder, strPath);
			String arrFiles[] = folder.list();
			if (arrFiles == null)
				continue;
			for (int nIdxFile = 0; nIdxFile < arrFiles.length; nIdxFile++)
			{
				String strFile = arrFiles[nIdxFile];
				File file = new File(folder, strFile);
				if (file.isFile() && (strExtn == null || strFile.endsWith(strExtn)))
				{
					strFile = file.getPath().substring(s_strRootFolderRealPath.length()).replace('\\', '/');
					lisFiles.add(strFile);
				}
			}

		}

		return lisFiles.iterator();
	}

	public static String findFile(String strPath, boolean bBaseFirst)
	{
		String strFilePath = null;
		int nCount = s_listLookupFolders.size();
		for (int nIdx = 0; nIdx < nCount; nIdx++)
		{
			String strResourcePath = null;
			if (bBaseFirst)
				strResourcePath = (String)s_listLookupFolders.get(nCount - nIdx - 1);
			else
				strResourcePath = (String)s_listLookupFolders.get(nIdx);
			if (s_bUnExplodedWarDeployment)
			{
				String strSearchPath = (new StringBuilder()).append(strResourcePath).append("/").append(strPath).toString();
				boolean fileExists = resourceExists(strSearchPath, null);
				if (!fileExists)
					continue;
				strFilePath = strSearchPath;
				break;
			}
			File folder = new File(s_strRootFolderRealPath, strResourcePath);
			if (!folder.exists())
				continue;
			File file = new File(folder, strPath);
			if (!file.isFile())
				continue;
			strFilePath = file.getPath().substring(s_strRootFolderRealPath.length()).replace('\\', '/');
			break;
		}

		return strFilePath;
	}

	public static boolean resourceExists(String resourcePath, String contextPath)
	{
		boolean exists = false;
		if (resourcePath == null)
			return false;
		resourcePath = ResponseHeaderControlFilter.restoreOriginalUrl(resourcePath, contextPath);
		if (s_mapUrls.containsKey(resourcePath))
		{
			exists = ((Boolean)s_mapUrls.get(resourcePath)).booleanValue();
		} else
		{
			exists = getResourceURL(resourcePath, contextPath) != null;
			s_mapUrls.put(resourcePath, Boolean.valueOf(exists));
		}
		return exists;
	}

	public static InputStream getResourceAsStream(String resourcePath, String contextPath)
	{
		if (!resourceExists(resourcePath, contextPath))
			return null;
		resourcePath = ResponseHeaderControlFilter.restoreOriginalUrl(resourcePath, contextPath);
		InputStream inStream = null;
		URL url = getResourceURL(resourcePath, contextPath);
		if (url != null)
			try
			{
				inStream = url.openStream();
			}
			catch (IOException e)
			{
				throw new WrapperRuntimeException((new StringBuilder()).append("Unable to open the URL: ").append(resourcePath).toString());
			}
		return inStream;
	}

	public static URL getResourceURL(String resourcePath, String contextPath)
	{
		URL url = null;
		if (TRACEENABLED)
			Trace.println((new StringBuilder()).append("search resource in the deployment path: ").append(resourcePath).toString());
		if (contextPath != null && resourcePath.startsWith(contextPath))
			url = getResourceURL(resourcePath.substring(contextPath.length()));
		if (url == null)
			url = getResourceURL(resourcePath);
		if (TRACEENABLED)
			Trace.println((new StringBuilder()).append("found resource in the deployment path: ").append(resourcePath).toString());
		return url;
	}

	private static URL getResourceURL(String strResourcePath)
	{
		URL url = null;
		try
		{
			if (s_bUnExplodedWarDeployment)
			{
				url = s_servletContext.getResource(patchResourcePath(strResourcePath));
			} else
			{
				File file = null;
				if (strResourcePath.startsWith(s_strRootFolderRealPath))
					file = new File(strResourcePath);
				else
					file = new File(s_strRootFolderRealPath, strResourcePath);
				if (file.exists())
					url = file.toURL();
			}
		}
		catch (MalformedURLException e)
		{
			throw new IllegalArgumentException((new StringBuilder()).append("Invalid resource format: ").append(strResourcePath).toString());
		}
		return url;
	}

	private static String patchResourcePath(String strResourcePath)
	{
		if (!strResourcePath.startsWith("/"))
			strResourcePath = (new StringBuilder()).append("/").append(strResourcePath).toString();
		return strResourcePath;
	}

	public static Iterator findAllFilesInWebApp(String strSearchPath, String strExtn)
	{
		List list = new ArrayList(100);
		findAllFiles(strSearchPath, list, strExtn);
		return list.iterator();
	}

	private static void findAllFiles(String strSearchPath, List lisFiles, String strExtn)
	{
		strSearchPath = patchResourcePath(strSearchPath);
		if (!strSearchPath.endsWith("/"))
			strSearchPath = (new StringBuilder()).append(strSearchPath).append("/").toString();
		Set resourcePaths = s_servletContext.getResourcePaths(strSearchPath);
		if (resourcePaths != null)
		{
			Iterator it = resourcePaths.iterator();
			do
			{
				if (!it.hasNext())
					break;
				String strFile = (String)it.next();
				if (strFile.endsWith("/"))
					findAllFiles(strFile, lisFiles, strExtn);
				else
				if (strExtn == null || strFile.endsWith(strExtn))
					lisFiles.add(strFile);
			} while (true);
		}
	}

}
