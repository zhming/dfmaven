// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConfigService.java

package com.gihow.dfc;

import com.gihow.util.StringUtil;

import java.util.*;

// Referenced classes of package com.documentum.web.formext.config:
//			IConfigRefreshListener, ConfigElement, IConfigElement, ScopeKey, 
//			IQualifier, Context, QualifierContext, PrimaryElementsByScopeDictionary, 
//			PrimaryElementsByLocationDictionary, ConfigFile, IInquisitiveQualifier, IConfigLookup, 
//			IConfigContext, IConfigReader, BoundedContextCache, Preview, 
//			IConfigLookupHookEx, IConfigLookupHook

public class ConfigService
	implements IConfigLookup, IConfigContext, Cloneable
{
	private static class ConfigFileInfo
	{

		String m_strFilePath;
		String m_strAppName;
		int m_nAppLevel;
		boolean m_fIsAppConfigFile;

		ConfigFileInfo(String strPathName, String strAppName, int nAppLevel, boolean fIsAppConfigFile)
		{
			m_strFilePath = strPathName;
			m_strAppName = strAppName;
			m_nAppLevel = nAppLevel;
			m_fIsAppConfigFile = fIsAppConfigFile;
		}
	}

	private class VersionedScopeElementScopeKeys
	{

		IConfigElement m_scopeElement;
		private Map m_scopeKeysByVersion;
		final ConfigService this$0;

		List get(String version)
		{
			List scopeKeyList = (List)m_scopeKeysByVersion.get(version);
			if (scopeKeyList == null)
			{
				if (version.indexOf(',') != -1)
					throw new IllegalStateException((new StringBuilder()).append("ConfigService: Only one 'version' scope value is allowed. Offending scope element: ").append(m_scopeElement.toString()).toString());
				if (version.startsWith("not "))
					throw new IllegalStateException((new StringBuilder()).append("ConfigService: 'not ' is not allowed as part of the 'version' scope value. Offending scope element: ").append(m_scopeElement.toString()).toString());
				if (version.equals("latest"))
					m_scopeElement.setAttributeValue("version", version);
				else
					m_scopeElement.setAttributeValue("version", (new StringBuilder()).append(version).append(",").append("fixed_").append(version).toString());
				scopeKeyList = getScopeElementScopeKeys(m_scopeElement);
				m_scopeKeysByVersion.put(version, scopeKeyList);
			}
			return scopeKeyList;
		}

		VersionedScopeElementScopeKeys(IConfigElement scopeElement)
		{
            super();
			this$0 = ConfigService.this;
			m_scopeElement = null;
			m_scopeKeysByVersion = new HashMap();
			m_scopeElement = scopeElement;
		}
	}

	public static class LookupFilter
	{

	//	private PrimaryElementsByScopeDictionary m_lookupByScope;
		private IConfigElement m_filterElement;
		private boolean m_bIsInitialized;
		private boolean m_negated;

		private synchronized void initialize()
		{
//			if (!m_bIsInitialized && ConfigService.s_progressiveSingleton.m_qualifiers != null)
//			{
//				m_lookupByScope = new PrimaryElementsByScopeDictionary((new StringBuilder()).append("filter-").append(m_filterElement.toString()).toString(), 7);
//				List scopeKeyList = ConfigService.s_progressiveSingleton.getScopeElementScopeKeys(m_filterElement);
//				for (int iScopeKey = 0; iScopeKey < scopeKeyList.size(); iScopeKey++)
//				{
//					ScopeKey scopeKey = (ScopeKey)scopeKeyList.get(iScopeKey);
//					m_lookupByScope.put("filter", scopeKey, m_filterElement);
//				}
//
//				m_lookupByScope.init(ConfigService.s_progressiveSingleton.m_qualifiers);
//				m_bIsInitialized = true;
//			}
		}

		public synchronized boolean showContents()
		{
//			if (!m_bIsInitialized)
//			{
//				initialize();
//				if (BoundedContextCache.isStarted())
//					BoundedContextCache.getInstance().clear();
//			}
//			Context context = ConfigService.getThreadContext();
//			if (Trace.CONFIGSERVICE)
//				Trace.println((new StringBuilder()).append("Config service: filter lookup '").append(m_filterElement.toString()).append("', context=").append(context.toString()).toString());
//			boolean bShowContents = m_lookupByScope.get("filter", ConfigService.s_progressiveSingleton.makeScopeKey(context)) != null;
//			if (m_negated)
//				bShowContents = !bShowContents;
//			if (Trace.CONFIGSERVICE)
//				Trace.println((new StringBuilder()).append(" - ").append(bShowContents ? "shown" : "hidden").toString());
//			return bShowContents;
            return false;
		}

		private List getScopeKeys()
		{
			return ConfigService.s_progressiveSingleton.getScopeElementScopeKeys(m_filterElement);
		}

		public String toString()
		{
			StringBuffer strBuff = new StringBuffer(128);
			if (m_negated)
				strBuff.append("negate-");
			strBuff.append(m_filterElement.toString());
			return strBuff.toString();
		}

		boolean isNegated()
		{
			return m_negated;
		}


		LookupFilter(IConfigElement filterElement, boolean negate)
		{
			m_filterElement = null;
			m_bIsInitialized = false;
			m_negated = false;
			m_filterElement = filterElement;
			m_negated = negate;
			initialize();
		}
	}

	private static class LookupHooks
		implements IConfigLookupHookEx
	{

		private ArrayList m_lookupPaths;
		private ArrayList m_hooks;
		private ArrayList m_bAbsoluteArgs;

		public void addLookupHook(String strLookupPath, Object hook, Boolean bAbsoluteArg)
		{
			if (hook == null)
			{
				throw new IllegalArgumentException("hook is a mandatory parameter");
			} else
			{
				m_lookupPaths.add(strLookupPath.toCharArray());
				m_hooks.add(hook);
				m_bAbsoluteArgs.add(bAbsoluteArg);
				return;
			}
		}

		public IConfigElement onLookupElement(String strElementPath, Context context)
		{
			IConfigElement configElement = null;
			int iHookIdx = 0;
			Object hookObj = null;
			for (; configElement == null && iHookIdx < m_hooks.size(); iHookIdx++)
			{
				String strLookupArg = getLookupArg(iHookIdx, strElementPath);
				if (strLookupArg == null)
					continue;
				hookObj = m_hooks.get(iHookIdx);
				if (hookObj instanceof IConfigLookupHookEx)
				{
					IConfigLookupHookEx hook = (IConfigLookupHookEx)hookObj;
					configElement = hook.onLookupElement(strLookupArg, context);
				}
			}

			return configElement;
		}



		public String onLookupString(String strElementPath, Context context)
		{
			String strResult = null;
			int iHookIdx = 0;
			Object hookObj = null;
			for (; strResult == null && iHookIdx < m_hooks.size(); iHookIdx++)
			{
				String strLookupArg = getLookupArg(iHookIdx, strElementPath);
				if (strLookupArg == null)
					continue;
				hookObj = m_hooks.get(iHookIdx);
				if (hookObj instanceof IConfigLookupHookEx)
				{
					IConfigLookupHookEx hook = (IConfigLookupHookEx)hookObj;
					strResult = hook.onLookupString(strLookupArg, context);
				} else
				{
					IConfigLookupHook hook = (IConfigLookupHook)hookObj;
					strResult = hook.onLookupString(strLookupArg, context);
				}
			}

			return strResult;
		}

		public Boolean onLookupBoolean(String strElementPath, Context context)
		{
			Boolean bResult = null;
			int iHookIdx = 0;
			Object hookObj = null;
			for (; bResult == null && iHookIdx < m_hooks.size(); iHookIdx++)
			{
				String strLookupArg = getLookupArg(iHookIdx, strElementPath);
				if (strLookupArg == null)
					continue;
				hookObj = m_hooks.get(iHookIdx);
				if (hookObj instanceof IConfigLookupHookEx)
				{
					IConfigLookupHookEx hook = (IConfigLookupHookEx)hookObj;
					bResult = hook.onLookupBoolean(strLookupArg, context);
				} else
				{
					IConfigLookupHook hook = (IConfigLookupHook)hookObj;
					bResult = hook.onLookupBoolean(strLookupArg, context);
				}
			}

			return bResult;
		}

		public Integer onLookupInteger(String strElementPath, Context context)
		{
			Integer iResult = null;
			int iHookIdx = 0;
			Object hookObj = null;
			for (; iResult == null && iHookIdx < m_hooks.size(); iHookIdx++)
			{
				String strLookupArg = getLookupArg(iHookIdx, strElementPath);
				if (strLookupArg == null)
					continue;
				hookObj = m_hooks.get(iHookIdx);
				if (hookObj instanceof IConfigLookupHookEx)
				{
					IConfigLookupHookEx hook = (IConfigLookupHookEx)hookObj;
					iResult = hook.onLookupInteger(strLookupArg, context);
				} else
				{
					IConfigLookupHook hook = (IConfigLookupHook)hookObj;
					iResult = hook.onLookupInteger(strLookupArg, context);
				}
			}

			return iResult;
		}

		private String getLookupArg(int iHookIdx, String strElementPath)
		{
			String strLookupArg = null;
			char elementPath[] = strElementPath.toCharArray();
			char lookupPath[] = (char[])(char[])m_lookupPaths.get(iHookIdx);
			int iCompare = comparePaths(lookupPath, elementPath);
			if (iCompare != -1)
				if (m_bAbsoluteArgs.get(iHookIdx) == Boolean.TRUE)
					strLookupArg = strElementPath;
				else
					strLookupArg = new String(elementPath, iCompare, elementPath.length - iCompare);
			return strLookupArg;
		}

		private int comparePaths(char lookupPath[], char elementPath[])
		{
			int iCompare = -1;
			int iLookupIdx = 0;
			int iElementIdx = 0;
			do
			{
				if (elementPath.length - iElementIdx < lookupPath.length - iLookupIdx)
					break;
				for (; iLookupIdx < lookupPath.length && lookupPath[iLookupIdx] == elementPath[iElementIdx]; iElementIdx++)
					iLookupIdx++;

				if (iElementIdx == elementPath.length || elementPath[iElementIdx] != '[')
					break;
				while (++iElementIdx < elementPath.length && elementPath[iElementIdx] != ']') ;
				iElementIdx++;
			} while (true);
			if (iLookupIdx < lookupPath.length && lookupPath[iLookupIdx] == '*')
				iCompare = iElementIdx;
			else
			if (iElementIdx == elementPath.length)
				iCompare = elementPath.length;
			return iCompare;
		}

		public LookupHooks()
		{
			m_lookupPaths = new ArrayList(5);
			m_hooks = new ArrayList(5);
			m_bAbsoluteArgs = new ArrayList(5);
		}
	}

	static abstract class ContextualProcessor
	{

		abstract void run();

		ContextualProcessor()
		{
		}
	}


	private PrimaryElementsByScopeDictionary m_primaryElementsByScope;
	private PrimaryElementsByLocationDictionary m_primaryElementsByLocation;
	private List m_appConfigFiles;
	private Map m_parentAppNames;
	private IQualifier m_qualifiers[];
	private Map m_qualifierContextNames;
	private Map m_qualifiersByScopeName;
	private LookupHooks m_lookupHooks;
	private IConfigReader m_configReader;
	private static ConfigService s_singleton;
	private static ConfigService s_progressiveSingleton;
	private static boolean s_bInitializing = false;
	private static boolean s_intializedOnce = false;
	private static IConfigReader s_configReader;
	private static ArrayList s_listRefreshListeners = new ArrayList(5);
	private static boolean s_isRefreshing = false;
	private static ThreadLocalVariable s_threadContext = new ThreadLocalVariable("ConfigServiceContext");
	private static ThreadLocal s_threadReentrantCount = new ThreadLocal();
//	private static final ConfigElement DUPLICATE_FILTERED_ELEMENT_MARKER = new ConfigElement("DuplicateFIlteredElementMarker", null);
	private int m_appLevel;
	public static boolean TRACE = true;
	public static final String APPCONFIG_FILENAME = "app.xml";
	public static final String CONFIG = "config";
	public static final String SCOPE = "scope";
	public static final String ID = "id";
	public static final String VERSION = "version";
	private static final String LATEST_VERSION_SCOPE = "version='latest'";
	public static final String LATEST = "latest";
	public static final String FIXED = "fixed_";
	public static final String EXTENDS = "extends";
	public static final String MODIFIES = "modifies";
	public static final String REQUIRED = "required";
	public static final String APPLICATION = "application";
	public static final String QUALIFIERS = "qualifiers";
	public static final String CONFIG_FOLDERNAME = "config";
	public static final String LOOKUP_HOOK_PATH = "LookupHookPath";
	public static final String LOOKUP_HOOK_ABSOLUTE_PATH = "LookupHookArgument";
	public static final String LOOKUP_HOOK_CLASS = "LookupHookClass";
	public static final String NOT_DEFINED = "notdefined";
	private static final int TOPMOST_LEVEL = 999;
	private HashMap m_mapModificationTargetConfigFiles;
	private ArrayList m_listConfigFileInfo;

	public static synchronized void setConfigReader(IConfigReader configReader)
	{
		s_configReader = configReader;
		if (configReader == null)
			s_singleton = null;
	}

	private static synchronized ConfigService getInstance()
	{
		if (s_singleton != null)
			return s_singleton;
		if (s_intializedOnce)
		{
			refresh();
            return s_singleton;
		}
		if (s_bInitializing)
			throw new RuntimeException("Re-entrant call to getInstance()");
		s_bInitializing = true;
		s_singleton = new ConfigService(s_configReader);
		s_bInitializing = false;
		s_intializedOnce = true;
		return s_singleton;
	}

	public static IConfigLookup getConfigLookup()
	{
		return getInstance();
	}

	public static IConfigContext getConfigContext()
	{
		return getInstance();
	}

	public static void refresh()
	{
		if (!proceedWithRefresh())
			return;
		ConfigService _singleton = new ConfigService(s_configReader);
		synchronized (ConfigService.class)
		{
			ArrayList refreshListeners = (ArrayList)s_listRefreshListeners.clone();
			s_singleton = _singleton;
			int nCount = refreshListeners.size();
			for (int i = 0; i < nCount; i++)
				((IConfigRefreshListener)refreshListeners.get(i)).onPostRefresh();

		}
		s_isRefreshing = false;
	}

	private static synchronized boolean proceedWithRefresh()
	{
		if (s_isRefreshing)
		{
			return false;
		} else
		{
			s_isRefreshing = true;
			return true;
		}
	}

	public static String[] getPrimaryElementIds(String strElementName)
	{
		//return getInstance().m_primaryElementsByScope.getPrimaryElementIds(strElementName);
        return new String[]{};
	}

	public static String[] getPrimaryElementScopes(String strElementName)
	{
		//return getInstance().m_primaryElementsByScope.getPrimaryElementScopes(strElementName);
        return new String[]{};
	}

	public static IConfigElement[] getPrimaryElements(String strElementName)
	{
		//return getInstance().m_primaryElementsByScope.getPrimaryElements(strElementName);
        return new IConfigElement[]{};
	}

	public static Iterator getUnfilteredChildElements(IConfigElement element)
	{
//		if (element instanceof ConfigElement)
//			return ((ConfigElement)element).getChildElements(true);
//		else
//			throw new IllegalStateException((new StringBuilder()).append("Cannot get unfiltered child elements of a non-ConfigElement: ").append(element.toString()).toString());
        return element.getChildElements();
	}

	public static void addRefreshListener(IConfigRefreshListener refreshListener)
	{
		s_listRefreshListeners.add(refreshListener);
	}

	public static void removeRefreshListener(IConfigRefreshListener refreshListener)
	{
		s_listRefreshListeners.remove(refreshListener);
	}

	public static void addConfigDOMObject(IConfigElement configDOMObj)
	{
		Iterator scopeElements = configDOMObj.getChildElements("scope");
		List primaryModifications = new ArrayList();
		while (scopeElements.hasNext()) 
		{
			IConfigElement scopeElement = (IConfigElement)scopeElements.next();
			String strScopeElementVersion = initialiseConfigDOMScopeElementVersion(scopeElement);
			String strAppName = scopeElement.getAttributeValue("application");
			if (strAppName == null || strAppName.length() == 0)
				scopeElement.setAttributeValue("application", s_configReader.getAppName());
			Iterator strAttributeNames = scopeElement.getAttributeNames();
			String strAttributeName = null;
			StringBuffer buf = null;
			boolean isValidQualifier = true;
			int i = 0;
			do
			{
				if (!strAttributeNames.hasNext())
					break;
				strAttributeName = (String)strAttributeNames.next();
				if (!getInstance().isScopeName(strAttributeName))
				{
					if (buf == null)
						buf = new StringBuffer(256);
					if (i > 0)
						buf.append(", ");
					i++;
					buf.append(strAttributeName);
					isValidQualifier = false;
				}
			} while (true);
			if (!isValidQualifier)
			{
				Trace.println((new StringBuilder()).append("No qualifier is defined for the scopes: ").append(buf.toString()).append(".  The element is not processed: ").append(configDOMObj).toString());
			} else
			{
				List scopeKeyList = null;
				Iterator primaryElements = scopeElement.getChildElements();
				while (primaryElements.hasNext()) 
				{
					IConfigElement primaryElement = (IConfigElement)primaryElements.next();
					String strModifies = primaryElement.getAttributeValue("modifies");
					if (strModifies != null)
					{
						primaryModifications.add(primaryElement);
					} else
					{
						if (scopeKeyList == null)
							scopeKeyList = getInstance().getScopeElementScopeKeys(scopeElement);
						primaryElement.setAttributeValue("version", strScopeElementVersion);
						String strPrimaryElementName = getInstance().getPrimaryElementName(primaryElement);
						PrimaryElementsByScopeDictionary primaryElemByScopeDict = getInstance().getPrimaryElementsByScope();
						synchronized (primaryElemByScopeDict)
						{
							for (int iScopeKey = 0; iScopeKey < scopeKeyList.size(); iScopeKey++)
							{
								ScopeKey scopeKey = (ScopeKey)scopeKeyList.get(iScopeKey);
								primaryElemByScopeDict.remove(strPrimaryElementName, scopeKey);
								primaryElemByScopeDict.put(strPrimaryElementName, scopeKey, primaryElement);
							}

						}
					}
				}
			}
		}
		getInstance().processModifications(primaryModifications, 999);
	}

	private static String initialiseConfigDOMScopeElementVersion(IConfigElement scopeElement)
	{
		String strScopeVersion = scopeElement.getAttributeValue("version");
		if (strScopeVersion == null || strScopeVersion.length() == 0)
		{
			strScopeVersion = "latest";
			scopeElement.setAttributeValue("version", "latest");
		}
		return strScopeVersion;
	}

	public boolean isContextName(String strName)
	{
		return m_qualifierContextNames.get(strName) != null;
	}

	public Iterator getContextNames()
	{
		return m_qualifierContextNames.keySet().iterator();
	}

	static String[] getScopeNames()
	{
		return getInstance()._getScopeNames();
	}

	private String[] _getScopeNames()
	{
		List scopeNames = new ArrayList();
		IQualifier arr$[] = m_qualifiers;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			IQualifier qualifier = arr$[i$];
			scopeNames.add(qualifier.getScopeName());
		}

		return (String[])scopeNames.toArray(new String[0]);
	}

	static String getDefaultScopeValue(String scopeName)
	{
		return getInstance()._getDefaultScopeValue(scopeName);
	}

	private String _getDefaultScopeValue(String scopeName)
	{
		IQualifier qualifier = (IQualifier)m_qualifiersByScopeName.get(scopeName);
		if (qualifier == null)
		{
			throw new IllegalArgumentException((new StringBuilder()).append("There is no qualifier defined for scope ").append(scopeName).toString());
		} else
		{
			Context emptyContext = new Context();
			return qualifier.getScopeValue(new QualifierContext(emptyContext));
		}
	}

	public IConfigElement lookupElement(String strElementPath, Context context)
	{
//		IConfigElement configElement;
//		Context originalContext;
//		boolean conditionalStarted;
//		boolean startTrackContext;
//		configElement = null;
//		if (Trace.CONFIGSERVICE)
//			Trace.println((new StringBuilder()).append("Config service: Lookup element '").append(strElementPath).append("', context=").append(context.toString()).toString());
//		if (strElementPath == null || strElementPath.length() == 0)
//			throw new IllegalArgumentException("ConfigService.lookupElement(...) 'strElementPath'");
//		originalContext = getThreadContext();
//		conditionalStarted = false;
//		startTrackContext = false;
//		IConfigElement iconfigelement;
//		if (incrementReentrentCount() == 1)
//		{
//			conditionalStarted = BoundedContextCache.conditionalStart();
//			if (!BoundedContextCache.getInstance().isContextTracked())
//			{
//				BoundedContextCache.getInstance().trackContext(true);
//				startTrackContext = true;
//			}
//		}
//		setThreadContext(context);
//		if (m_lookupHooks != null)
//			configElement = m_lookupHooks.onLookupElement(strElementPath, context);
//		if (configElement == null)
//		{
//			String strPrimaryElement = strElementPath;
//			String strLesserElements = null;
//			int iSeparator = strElementPath.indexOf('.');
//			if (iSeparator != -1)
//			{
//				strPrimaryElement = strElementPath.substring(0, iSeparator);
//				strLesserElements = strElementPath.substring(iSeparator + 1);
//			}
//			IConfigElement primaryElement = m_primaryElementsByScope.get(strPrimaryElement, makeScopeKey(context));
//			if (primaryElement != null)
//			{
//				if (Trace.CONFIGSERVICE)
//					Trace.println("\tresolving inheritance:");
//				do
//				{
//					if (Trace.CONFIGSERVICE)
//						Trace.println((new StringBuilder()).append("\t\t").append(getConfigFilePathName(primaryElement)).toString());
//					if (strLesserElements != null)
//					{
//						configElement = primaryElement.getDescendantElement(strLesserElements);
//						if (configElement == null)
//							primaryElement = getExtendedPrimaryElement(primaryElement);
//					} else
//					{
//						configElement = primaryElement;
//					}
//				} while (configElement == null && primaryElement != null);
//			}
//		}
//		traceLookupResult(configElement);
//		iconfigelement = configElement;
//		int count = decrementReentrentCount();
//		if (count == 0)
//		{
//			if (startTrackContext)
//			{
//				startTrackContext = false;
//				BoundedContextCache.getInstance().trackContext(false);
//			}
//			if (conditionalStarted)
//				BoundedContextCache.end();
//		} else
//		if (originalContext != null)
//			setThreadContext(originalContext);
//		return iconfigelement;
//		Exception exception;
//		exception;
//		int count = decrementReentrentCount();
//		if (count == 0)
//		{
//			if (startTrackContext)
//			{
//				startTrackContext = false;
//				BoundedContextCache.getInstance().trackContext(false);
//			}
//			if (conditionalStarted)
//				BoundedContextCache.end();
//		} else
//		if (originalContext != null)
//			setThreadContext(originalContext);
//		throw exception;
        return null;
	}

	public String lookupString(String strElementPath, Context context)
	{
		String strResult = null;
		if (strElementPath == null || strElementPath.length() == 0)
			throw new IllegalArgumentException("ConfigService.lookupString(...) 'strElementPath'");
		if (m_lookupHooks != null)
			strResult = m_lookupHooks.onLookupString(strElementPath, context);
		if (strResult == null)
		{
			IConfigElement element = lookupElement(strElementPath, context);
			if (element != null)
				strResult = element.getValue();
		}
		return strResult;
	}



	public Boolean lookupBoolean(String strElementPath, Context context)
	{
		Boolean bResult = null;
		if (strElementPath == null || strElementPath.length() == 0)
			throw new IllegalArgumentException("ConfigService.lookupBoolean(...) 'strElementPath'");
		if (m_lookupHooks != null)
			bResult = m_lookupHooks.onLookupBoolean(strElementPath, context);
		if (bResult == null)
		{
			IConfigElement element = lookupElement(strElementPath, context);
			if (element != null)
				bResult = element.getValueAsBoolean();
		}
		return bResult;
	}

	public Integer lookupInteger(String strElementPath, Context context)
	{
		Integer iResult = null;
		if (strElementPath == null || strElementPath.length() == 0)
			throw new IllegalArgumentException("ConfigService.lookupInteger(...) 'strElementPath'");
		if (m_lookupHooks != null)
			iResult = m_lookupHooks.onLookupInteger(strElementPath, context);
		if (iResult == null)
		{
			IConfigElement element = lookupElement(strElementPath, context);
			if (element != null)
				iResult = element.getValueAsInteger();
		}
		return iResult;
	}

	static String getAppName()
	{
		return s_progressiveSingleton.m_configReader.getAppName();
	}

	static String getParentAppName(String strAppName)
	{
		return (String)s_progressiveSingleton.m_parentAppNames.get(strAppName);
	}

	static LookupFilter getLookupFilter(IConfigElement filterElement, boolean negate)
	{
		return new LookupFilter(filterElement, negate);
	}

	static IConfigElement getMostRelevantFilterElement(Map filterToElementMap)
	{
		ConfigService configService = getInstance();
		Map scopeKeyToElementMap = new HashMap();
		Set entrySet = filterToElementMap.entrySet();
		IConfigElement element;
		for (Iterator i$ = entrySet.iterator(); i$.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i$.next();
			LookupFilter filter = (LookupFilter)entry.getKey();
			element = (IConfigElement)entry.getValue();
			List scopeKeyList = filter.getScopeKeys();
			Iterator iter = scopeKeyList.iterator();
			while (iter.hasNext())
			{
				ScopeKey key = (ScopeKey)iter.next();
				if (scopeKeyToElementMap.containsKey(key))
					scopeKeyToElementMap.put(key, "");
				else
					scopeKeyToElementMap.put(key, element);
			}
		}

		PrimaryElementsByScopeDictionary lookupByScope = new PrimaryElementsByScopeDictionary("Most Relevant Filter", 7);
		Set keySet = scopeKeyToElementMap.keySet();
		ScopeKey key;
		for (Iterator i$ = keySet.iterator(); i$.hasNext(); lookupByScope.put("filter", key, (IConfigElement)scopeKeyToElementMap.get(key)))
			key = (ScopeKey)i$.next();

		lookupByScope.init(configService.m_qualifiers);
		Context context = getThreadContext();
//		key = lookupByScope.get("filter", configService.makeScopeKey(context));
//		if (key == DUPLICATE_FILTERED_ELEMENT_MARKER)
//			return null;
//		else
//			return key;
        return null;
	}

	private ConfigService(IConfigReader configReader)
	{
		m_appConfigFiles = new ArrayList(100);
		m_parentAppNames = new HashMap(17, 1.0F);
		m_qualifierContextNames = new HashMap(7, 1.0F);
		m_qualifiersByScopeName = new HashMap(7, 1.0F);
		m_appLevel = 0;
		m_mapModificationTargetConfigFiles = new HashMap();
		m_listConfigFileInfo = new ArrayList(256);
		s_progressiveSingleton = this;
		m_configReader = configReader;
		//String strAppName = m_configReader.getAppName();
		m_primaryElementsByScope = new PrimaryElementsByScopeDictionary("scope-global", 4001);
		m_primaryElementsByLocation = new PrimaryElementsByLocationDictionary(227);
		//initialiseApp(strAppName, null);
		//m_primaryElementsByScope.init(m_qualifiers);
		if (Trace.CONFIGSERVICE)
		{
			m_primaryElementsByScope.trace();
			m_primaryElementsByLocation.trace();
		}
		initialiseLookupHooks();
	}

	private static ConfigFile getConfigFile(IConfigElement configElement)
	{
		for (; configElement.getParent() != null; configElement = configElement.getParent());
		if (configElement instanceof ConfigFile)
			return (ConfigFile)configElement;
		else
			return null;
	}

	static String getConfigFilePathName(IConfigElement configElement)
	{
		ConfigFile configFile = getConfigFile(configElement);
		if (configFile != null)
			return configFile.getPathName();
		else
			return null;
	}

	private PrimaryElementsByScopeDictionary getPrimaryElementsByScope()
	{
		return m_primaryElementsByScope;
	}

	private String getPrimaryElementName(IConfigElement primaryElement)
	{
		String strPrimaryElementName = primaryElement.getName();
		String strId = primaryElement.getAttributeValue("id");
		if (strId != null && strId.length() > 0)
		{
			StringBuffer buff = new StringBuffer(128);
			buff.append(strPrimaryElementName);
			buff.append("[id=");
			buff.append(strId);
			buff.append(']');
			strPrimaryElementName = buff.toString();
		}
		return strPrimaryElementName;
	}

	private void initialiseApp(String strAppName, String strDerivedAppName)
	{
		ResourceFileUtil.addLookupFolder(strAppName, false);
		if (strDerivedAppName == null || strDerivedAppName.length() == 0)
			ResourceFileUtil.addLookupFolder("", false);
		if (strDerivedAppName != null)
			m_parentAppNames.put(strDerivedAppName, strAppName);
		ConfigFile appConfigFile = m_configReader.loadAppConfigFile(strAppName);
		m_appConfigFiles.add(appConfigFile);
		IConfigElement scopeElement = appConfigFile.getChildElement("scope");
		if (scopeElement == null)
			throw new IllegalStateException("Missing <scope> tag in application file");
		IConfigElement appElement = scopeElement.getChildElement("application");
		if (appElement == null)
			throw new IllegalStateException("Missing <application> tag in application file");
		String strExtendsAppName = appElement.getAttributeValue("extends");
		if (strExtendsAppName != null)
		{
			int iConfigXML = strExtendsAppName.indexOf("app.xml");
			if (iConfigXML > 0)
				strExtendsAppName = strExtendsAppName.substring(0, iConfigXML - 1);
			initialiseApp(strExtendsAppName, strAppName);
		} else
		{
			initialiseQualifiers();
		}
		m_listConfigFileInfo.add(new ConfigFileInfo(appConfigFile.getPathName(), strAppName, m_appLevel, true));
		initialiseAppConfigFile(appConfigFile, strAppName, false);
		List primaryModifications = new ArrayList();
		ConfigFile configFile;
		for (Iterator configFiles = m_configReader.loadConfigFiles(strAppName); configFiles.hasNext(); primaryModifications.addAll(initialiseConfigFile(configFile, strAppName)))
		{
			configFile = (ConfigFile)configFiles.next();
			m_listConfigFileInfo.add(new ConfigFileInfo(configFile.getPathName(), strAppName, m_appLevel, false));
		}

		processModifications(primaryModifications, m_appLevel);
		m_appLevel++;
	}

	private IConfigElement initialiseAppConfigFile(ConfigFile appConfigFile, String strAppName, boolean fIsReload)
	{
		if (!fIsReload)
		{
			if (m_appConfigFiles.indexOf(appConfigFile) < 0)
				throw new IllegalStateException("an app.xml is not found in the existing list for initialization.");
		} else
		{
			boolean fDoneReplace = false;
			String strFilePath = appConfigFile.getPathName();
			int idxAppConfigFile = 0;
			do
			{
				if (idxAppConfigFile >= m_appConfigFiles.size())
					break;
				ConfigFile existingAppConfigFile = (ConfigFile)m_appConfigFiles.get(idxAppConfigFile);
				if (existingAppConfigFile.getPathName().equals(strFilePath))
				{
					m_appConfigFiles.set(idxAppConfigFile, appConfigFile);
					fDoneReplace = true;
					break;
				}
				idxAppConfigFile++;
			} while (true);
			if (!fDoneReplace)
				throw new IllegalStateException("an app.xml for refreshing is not found.");
		}
		IConfigElement scopeElement = appConfigFile.getChildElement("scope");
		if (scopeElement == null)
			throw new IllegalStateException("Missing <scope> tag in application file");
		IConfigElement appElement = scopeElement.getChildElement("application");
		if (appElement == null)
		{
			throw new IllegalStateException("Missing <application> tag in application file");
		} else
		{
			scopeElement.setAttributeValue("application", strAppName);
			String appElementName = appElement.getName();
			ScopeKey appScopeKey = new ScopeKey((new StringBuilder()).append("application='").append(strAppName).append("'").toString(), m_qualifiers);
			String strRootFolderPath = m_configReader.getRootFolderPath();
			String strAppRelFilePath = appConfigFile.getPathName().substring(strRootFolderPath.length());
			m_primaryElementsByScope.put(appElementName, appScopeKey, appElement);
			m_primaryElementsByLocation.put(appElementName, strAppRelFilePath, appElement);
			return appElement;
		}
	}

	private List initialiseConfigFile(ConfigFile configFile, String strAppName)
	{
		List primaryModifications = new ArrayList();
		String strRootFolderPath = m_configReader.getRootFolderPath();
		String strRelFilePath = configFile.getPathName().substring(strRootFolderPath.length());
		for (Iterator scopeElements = configFile.getChildElements("scope"); scopeElements.hasNext();)
		{
			IConfigElement scopeElement = (IConfigElement)scopeElements.next();
			for (Iterator strAttributeNames = scopeElement.getAttributeNames(); strAttributeNames.hasNext();)
			{
				String strAttributeName = (String)strAttributeNames.next();
				if (!isScopeName(strAttributeName))
					throw new IllegalStateException((new StringBuilder()).append("There is no qualifier defined for the scope: ").append(strAttributeName).append(" in element: ").append(scopeElement.toString()).toString());
			}

			scopeElement.setAttributeValue("application", strAppName);
			String strScopeElementVersion = scopeElement.getAttributeValue("version");
			VersionedScopeElementScopeKeys versionedScopeKeys = new VersionedScopeElementScopeKeys(scopeElement);
			Iterator primaryElements = scopeElement.getChildElements();
			while (primaryElements.hasNext()) 
			{
				IConfigElement primaryElement = (IConfigElement)primaryElements.next();
				String modifies = primaryElement.getAttributeValue("modifies");
				if (modifies != null)
				{
					primaryModifications.add(primaryElement);
				} else
				{
					String strPrimaryElementVersion = initialisePrimaryElementVersion(strAppName, strScopeElementVersion, primaryElement, strRelFilePath);
					List scopeKeyList = versionedScopeKeys.get(strPrimaryElementVersion);
					String strPrimaryElementName = getPrimaryElementName(primaryElement);
					for (int iScopeKey = 0; iScopeKey < scopeKeyList.size(); iScopeKey++)
					{
						ScopeKey scopeKey = (ScopeKey)scopeKeyList.get(iScopeKey);
						m_primaryElementsByScope.put(strPrimaryElementName, scopeKey, primaryElement);
					}

					if (!isMarkedAsNotDefined(primaryElement))
						m_primaryElementsByLocation.put(strPrimaryElementName, strRelFilePath, primaryElement);
				}
			}
		}

		return primaryModifications;
	}

	private void processModifications(List primaryModifications, int appLevel)
	{
		for (Iterator i$ = primaryModifications.iterator(); i$.hasNext();)
		{
			IConfigElement primaryModification = (IConfigElement)i$.next();
			String strId = primaryModification.getAttributeValue("id");
			if (strId != null && strId.length() > 0)
				throw new IllegalStateException((new StringBuilder()).append("id attribute is not allowed in modification element: ").append(primaryModification).toString());
			IConfigElement modifiedPrimaryElement = getModifiedPrimaryElement(primaryModification);
			if ((modifiedPrimaryElement instanceof ConfigElement) && (primaryModification instanceof ConfigElement))
			{
				ConfigFile configFile = getConfigFile(primaryModification);
				if (configFile != null && configFile.getPathName() != null)
					m_mapModificationTargetConfigFiles.put(configFile.getPathName(), configFile);
				Set setModificationTarget = ((ConfigElement)modifiedPrimaryElement).applyModification((ConfigElement)primaryModification, appLevel);
				Iterator iter = setModificationTarget.iterator();
				while (iter.hasNext())
				{
					ConfigElement targetElement = (ConfigElement)iter.next();
					configFile = getConfigFile(targetElement);
					if (configFile != null && configFile.getPathName() != null)
						m_mapModificationTargetConfigFiles.put(configFile.getPathName(), configFile);
				}
			} else
			if (!(modifiedPrimaryElement instanceof ConfigElement))
				throw new IllegalStateException((new StringBuilder()).append("Cannot apply modification to a non-ConfigElement: ").append(modifiedPrimaryElement).toString());
			else
				throw new IllegalStateException((new StringBuilder()).append("Cannot appy a non-ConfigElement modification: ").append(primaryModification).toString());
		}

	}

	private boolean isScopeName(String strName)
	{
		return m_qualifiersByScopeName.get(strName) != null;
	}

	private IConfigElement getQualifiersElement()
	{
		IConfigElement qualifiersElement = null;
		int iAppConfig = 0;
		do
		{
			if (iAppConfig >= m_appConfigFiles.size())
				break;
			ConfigFile configFile = (ConfigFile)m_appConfigFiles.get(iAppConfig);
			IConfigElement scopeElement = configFile.getChildElement("scope");
			IConfigElement appElement = scopeElement.getChildElement("application");
			qualifiersElement = appElement.getChildElement("qualifiers");
			if (qualifiersElement != null)
				break;
			iAppConfig++;
		} while (true);
		if (qualifiersElement == null)
			throw new IllegalStateException("No qualifiers are defined");
		else
			return qualifiersElement;
	}

	private void initialiseQualifiers()
	{
		if (Trace.CONFIGSERVICE)
			Trace.println("ConfigService: Initialising qualifiers:");
		IConfigElement appQualifiers = getQualifiersElement();
		Iterator iterQualifierDefs = appQualifiers.getChildElements("qualifier");
		Vector qualifiers = new Vector();
		IQualifier qualifier;
		for (; iterQualifierDefs.hasNext(); qualifiers.add(qualifier))
		{
			IConfigElement qualifierDef = (IConfigElement)iterQualifierDefs.next();
			String strQualifierClass = qualifierDef.getValue();
			if (strQualifierClass == null || strQualifierClass.length() == 0)
				throw new IllegalStateException("Missing qualifier <class> element");
			Class qualifierClass = null;
			qualifier = null;
			try
			{
				qualifierClass = Class.forName(strQualifierClass);
				Class IQualifierClass = Class.forName("com.documentum.web.formext.config.IQualifier");
				Class qualifierInterfaces[] = qualifierClass.getInterfaces();
				boolean bImplementsIQualifier = false;
				int iInterface = 0;
				do
				{
					if (iInterface >= qualifierInterfaces.length)
						break;
					if (qualifierInterfaces[iInterface].equals(IQualifierClass))
					{
						bImplementsIQualifier = true;
						break;
					}
					iInterface++;
				} while (true);
				if (!bImplementsIQualifier)
					throw new IllegalStateException((new StringBuilder()).append("Class does not implement IQualifier: ").append(strQualifierClass).toString());
				qualifier = (IQualifier)qualifierClass.newInstance();
			}
			catch (ClassNotFoundException e)
			{
				throw new WrapperRuntimeException((new StringBuilder()).append("Qualifier class not found: ").append(strQualifierClass).toString(), e);
			}
			catch (IllegalAccessException e)
			{
				throw new WrapperRuntimeException((new StringBuilder()).append("Unable to access qualifier class: ").append(strQualifierClass).toString(), e);
			}
			catch (InstantiationException e)
			{
				throw new WrapperRuntimeException((new StringBuilder()).append("Unable to instantiate qualifier class: ").append(strQualifierClass).toString(), e);
			}
			m_qualifiersByScopeName.put(qualifier.getScopeName(), qualifier);
			String strContextNames[] = qualifier.getContextNames();
			if (strContextNames != null)
			{
				for (int iContextName = 0; iContextName < strContextNames.length; iContextName++)
					m_qualifierContextNames.put(strContextNames[iContextName], "");

			}
			if (Trace.CONFIGSERVICE)
				Trace.println((new StringBuilder()).append("\t").append(strQualifierClass).toString());
		}

		m_qualifiers = new IQualifier[qualifiers.size()];
		for (int iQualifier = 0; iQualifier < qualifiers.size(); iQualifier++)
			m_qualifiers[iQualifier] = (IQualifier)qualifiers.elementAt(iQualifier);

	}

	private void initialiseLookupHooks()
	{
		ResourceBundle environmentProps = null;
		try
		{
			environmentProps = ResourceBundle.getBundle("com.documentum.web.formext.Environment");
		}
		catch (MissingResourceException e)
		{
			if (Trace.CONFIGSERVICE)
				Trace.println("ConfigService: Environment.properties file not found");
		}
		if (environmentProps != null)
		{
			int iLookupHookIdx = 1;
			do
			{
				String strLookupPath = null;
				String strHookClass = null;
				Boolean bAbsoluteArg = null;
				String strLookupPathKey = (new StringBuilder()).append("LookupHookPath.").append(iLookupHookIdx).toString();
				try
				{
					strLookupPath = environmentProps.getString(strLookupPathKey);
				}
				catch (MissingResourceException e)
				{
					if (Trace.CONFIGSERVICE)
						Trace.println((new StringBuilder()).append("Config Service: ").append(strLookupPathKey).append(" not defined in environment.properties").toString());
				}
				if (strLookupPath == null || strLookupPath.length() == 0)
					break;
				String strLookupClassKey = (new StringBuilder()).append("LookupHookClass.").append(iLookupHookIdx).toString();
				try
				{
					strHookClass = environmentProps.getString(strLookupClassKey);
				}
				catch (MissingResourceException e) { }
				if (strHookClass == null || strHookClass.length() == 0)
					throw new WrapperRuntimeException((new StringBuilder()).append("Config Service: ").append(strLookupClassKey).append(" not defined in environment.properties").toString());
				Object lookupHook = null;
				try
				{
					lookupHook = Class.forName(strHookClass).newInstance();
					if (Trace.CONFIGSERVICE)
						Trace.println((new StringBuilder()).append("Config Service: Lookup Hook ").append(strHookClass).append(" created for path ").append(strLookupPath).toString());
				}
				catch (ClassNotFoundException e)
				{
					throw new WrapperRuntimeException((new StringBuilder()).append("ConfigLookupHook class not found: ").append(strHookClass).toString(), e);
				}
				catch (IllegalAccessException e)
				{
					throw new WrapperRuntimeException((new StringBuilder()).append("Unable to access ConfigLookupHook class: ").append(strHookClass).toString(), e);
				}
				catch (InstantiationException e)
				{
					throw new WrapperRuntimeException((new StringBuilder()).append("Unable to instantiate ConfigLookupHook class: ").append(strHookClass).toString(), e);
				}
				bAbsoluteArg = Boolean.TRUE;
				String strLookupAbsoluteKey = (new StringBuilder()).append("LookupHookArgument.").append(iLookupHookIdx).toString();
				try
				{
					String strHookAbsoluteArg = environmentProps.getString(strLookupAbsoluteKey);
					if (strHookAbsoluteArg != null && strHookAbsoluteArg.equals("relative"))
						bAbsoluteArg = Boolean.FALSE;
				}
				catch (MissingResourceException e) { }
				if (m_lookupHooks == null)
					m_lookupHooks = new LookupHooks();
				m_lookupHooks.addLookupHook(strLookupPath, lookupHook, bAbsoluteArg);
				iLookupHookIdx++;
			} while (true);
		}
	}

	private ScopeKey makeScopeKey(Context context)
	{
		ScopeKey key = null;
		BoundedContextCache cache = BoundedContextCache.getInstance();
		boolean trackContext = cache != null && cache.isContextTracked();
		if (trackContext)
			key = cache.getScopeKey(context);
		if (key == null)
		{
			key = new ScopeKey(new QualifierContext(context), m_qualifiers);
			if (trackContext)
				cache.setScopeKey(context, key);
		}
		return key;
	}

	private IConfigElement getExtendedPrimaryElement(IConfigElement primaryElement)
	{
		String strExtends = primaryElement.getAttributeValue("extends");
		if (strExtends == null || strExtends.length() == 0)
		{
			primaryElement = null;
		} else
		{
			StringBuffer buf = new StringBuffer(128);
			buf.append(primaryElement.getName());
			int iColon = strExtends.indexOf(':');
			if (iColon != -1)
			{
				buf.append("[id=");
				buf.append(strExtends.substring(0, iColon));
				buf.append(']');
				strExtends = strExtends.substring(iColon + 1);
			} else
			{
				String strId = primaryElement.getAttributeValue("id");
				if (strId != null)
				{
					buf.append("[id=");
					buf.append(strId);
					buf.append(']');
				}
			}
			String strPrimaryElement = buf.toString();
			if (strExtends.indexOf('=') != -1)
			{
				String strEnforcedExtends = enforceVersionScopeForDerive(strExtends);
				primaryElement = m_primaryElementsByScope.getAbsolute(strPrimaryElement, new ScopeKey(strEnforcedExtends, m_qualifiers));
			} else
			{
				primaryElement = m_primaryElementsByLocation.get(strPrimaryElement, strExtends);
			}
			if (primaryElement == null)
				throw new IllegalStateException((new StringBuilder()).append("ConfigService: Cannot locate ").append(strPrimaryElement).append(" in extends=").append(strExtends).toString());
		}
		return primaryElement;
	}

	private IConfigElement getModifiedPrimaryElement(IConfigElement primaryModification)
	{
		IConfigElement modifiedPrimaryElement = null;
		String strModifies = primaryModification.getAttributeValue("modifies");
		if (strModifies == null || strModifies.length() == 0)
			throw new IllegalStateException((new StringBuilder()).append("Missing or empty modifies attribute in: ").append(primaryModification).toString());
		StringBuffer buf = new StringBuffer(128);
		buf.append(primaryModification.getName());
		int iColon = strModifies.indexOf(':');
		if (iColon != -1)
		{
			buf.append("[id=");
			buf.append(strModifies.substring(0, iColon));
			buf.append(']');
			strModifies = strModifies.substring(iColon + 1);
		}
		String strPrimaryElement = buf.toString();
		if (strModifies.indexOf('=') != -1)
		{
			String strEnforcedModifies = enforceVersionScopeForDerive(strModifies);
			modifiedPrimaryElement = m_primaryElementsByScope.getAbsolute(strPrimaryElement, new ScopeKey(strEnforcedModifies, m_qualifiers));
		} else
		{
			modifiedPrimaryElement = m_primaryElementsByLocation.get(strPrimaryElement, strModifies);
		}
		if (modifiedPrimaryElement == null)
			throw new IllegalStateException((new StringBuilder()).append("ConfigService: Cannot locate ").append(strPrimaryElement).append(" in modifies=").append(primaryModification).toString());
		else
			return modifiedPrimaryElement;
	}

	private String enforceVersionScopeForDerive(String strDerives)
	{
		if (strDerives.indexOf("version=") == -1)
			return (new StringBuilder()).append(strDerives).append(" ").append("version='latest'").toString();
		else
			return strDerives;
	}

	private String initialisePrimaryElementVersion(String strAppName, String scopeElementVersion, IConfigElement primaryElement, String strRelFilePath)
	{
		if (scopeElementVersion != null)
			scopeElementVersion = scopeElementVersion.trim();
		String primaryElementVersion = null;
		if (scopeElementVersion != null && scopeElementVersion.length() > 0)
		{
			primaryElementVersion = scopeElementVersion;
		} else
		{
			String strExtends = primaryElement.getAttributeValue("extends");
			if (strExtends != null && strExtends.length() > 0 && strExtends.indexOf('=') == -1)
			{
				int iColon = strExtends.indexOf(':');
				String id = primaryElement.getAttributeValue("id");
				String extendedId = null;
				if (iColon != -1)
				{
					extendedId = strExtends.substring(0, iColon);
					strExtends = strExtends.substring(iColon + 1);
				} else
				{
					extendedId = id;
				}
				if (id != null && id.equals(extendedId))
				{
					strExtends = strExtends.replace('\\', '/');
					if (strExtends.charAt(0) == '/')
						strExtends = strExtends.substring(1);
					boolean isExtendedFromParentApp = false;
					String app = strAppName;
					do
					{
						String parentApp = getParentAppName(app);
						if (parentApp != null && strExtends.startsWith(parentApp))
							isExtendedFromParentApp = true;
						else
							app = parentApp;
					} while (app != null && !isExtendedFromParentApp);
					if (isExtendedFromParentApp)
					{
						StringBuffer buf = new StringBuffer(128);
						buf.append(primaryElement.getName());
						buf.append("[id=");
						buf.append(extendedId);
						buf.append(']');
						String strPrimaryElement = buf.toString();
						IConfigElement extendedPrimaryElement = m_primaryElementsByLocation.get(strPrimaryElement, strExtends);
						if (extendedPrimaryElement == null)
							throw new IllegalStateException((new StringBuilder()).append("ConfigService: Cannot locate ").append(strPrimaryElement).append(" in extends=").append(strExtends).append(" while trying to retrieve the inherited version for ").append(strPrimaryElement).append(" in ").append(strRelFilePath).toString());
						primaryElementVersion = extendedPrimaryElement.getAttributeValue("version");
					}
				}
			}
			if (primaryElementVersion == null)
				primaryElementVersion = "latest";
		}
		primaryElement.setAttributeValue("version", primaryElementVersion);
		if (Trace.CONFIGSERVICE && !primaryElementVersion.equals("latest"))
		{
			String performedOn;
			if (scopeElementVersion != null && scopeElementVersion.length() > 0)
				performedOn = "specified for ";
			else
				performedOn = "inherited by ";
			Trace.println((new StringBuilder()).append("Config Service: Version ").append(primaryElementVersion).append(" is ").append(performedOn).append(primaryElement.getName()).append("[id=").append(primaryElement.getAttributeValue("id")).append("] in ").append(strRelFilePath).toString());
		}
		return primaryElementVersion;
	}

	public static IConfigElement getExtendedElement(IConfigElement primaryElement)
	{
		return getInstance().getExtendedPrimaryElement(primaryElement);
	}

	private List getScopeElementScopeKeys(IConfigElement scopeElement)
	{
		List scopeKeys = new ArrayList();
		List scopeValues[] = new List[m_qualifiers.length];
		for (int iQualifier = 0; iQualifier < m_qualifiers.length; iQualifier++)
		{
			IQualifier qualifier = m_qualifiers[iQualifier];
			scopeValues[iQualifier] = new ArrayList();
			String strScopeName = qualifier.getScopeName();
			String strScopeValue = scopeElement.getAttributeValue(strScopeName);
			if (strScopeValue != null && strScopeValue.length() > 0)
				scopeValues[iQualifier] = parseScopeValueClause(m_qualifiers[iQualifier], strScopeValue);
			boolean bAddGenericScopeValue = true;
			for (int iScopeValue = 0; iScopeValue < scopeValues[iQualifier].size(); iScopeValue++)
			{
				strScopeValue = (String)scopeValues[iQualifier].get(iScopeValue);
				if (!strScopeValue.toLowerCase().startsWith("not "))
					bAddGenericScopeValue = false;
			}

			if (bAddGenericScopeValue)
				scopeValues[iQualifier].add("*");
		}

		int nIndex[] = new int[m_qualifiers.length];
		int nCount[] = new int[m_qualifiers.length];
		for (int iQualifier = 0; iQualifier < m_qualifiers.length; iQualifier++)
		{
			nIndex[iQualifier] = 0;
			nCount[iQualifier] = scopeValues[iQualifier].size();
		}

		boolean bFinished = false;
		do
		{
			String strScopeKey[] = new String[m_qualifiers.length];
			for (int iQualifier = 0; iQualifier < m_qualifiers.length; iQualifier++)
				strScopeKey[iQualifier] = (String)scopeValues[iQualifier].get(nIndex[iQualifier]);

			scopeKeys.add(new ScopeKey(strScopeKey, m_qualifiers));
			for (int iQualifier = 0; iQualifier < m_qualifiers.length; iQualifier++)
			{
				nIndex[iQualifier]++;
				if (nIndex[iQualifier] < nCount[iQualifier])
					break;
				nIndex[iQualifier] = 0;
				if (iQualifier == m_qualifiers.length - 1)
					bFinished = true;
			}

		} while (!bFinished);
		return scopeKeys;
	}

	private List parseScopeValueClause(IQualifier qualifier, String strScopeValueClause)
	{
		List scopeValues = new ArrayList();
		_parseScopeValueClause(qualifier, strScopeValueClause, false, scopeValues);
		if (qualifier instanceof IInquisitiveQualifier)
			((IInquisitiveQualifier)qualifier).onAddScopeValues(Collections.unmodifiableList(scopeValues));
		return scopeValues;
	}

	private void _parseScopeValueClause(IQualifier qualifier, String strScopeValueClause, boolean bNegate, List scopeValues)
	{
		strScopeValueClause = StringUtil.replace(strScopeValueClause, ",,", "(}]");
		StringTokenizer iterValues = new StringTokenizer(strScopeValueClause, ",");
		do
		{
			if (!iterValues.hasMoreTokens())
				break;
			String strValue = StringUtil.replace(iterValues.nextToken().trim(), "(}]", ",");
			boolean bNegateClause = bNegate;
			if (strValue.toLowerCase().startsWith("not "))
			{
				strValue = strValue.substring(3).trim();
				bNegateClause = !bNegateClause;
			}
			String strEntry = bNegateClause ? (new StringBuilder()).append("not ").append(strValue).toString() : strValue;
			if (!scopeValues.contains(strEntry))
				scopeValues.add(strEntry);
			String strEquivalentValues[] = qualifier.getAliasScopeValues(strValue);
			if (strEquivalentValues != null)
			{
				int i = 0;
				while (i < strEquivalentValues.length) 
				{
					_parseScopeValueClause(qualifier, strEquivalentValues[i], bNegateClause, scopeValues);
					i++;
				}
			}
		} while (true);
	}

	static boolean isMarkedAsNotDefined(IConfigElement primaryElement)
	{
		String strNotDefined = primaryElement.getAttributeValue("notdefined");
		if (strNotDefined == null || strNotDefined.equalsIgnoreCase("false"))
			return false;
		Iterator iter = primaryElement.getChildElements();
		if (primaryElement.getValue().length() != 0 || iter != null && iter.hasNext())
			throw new IllegalStateException((new StringBuilder()).append("Non defined elements cannot have children or values: ").append(primaryElement.toString()).toString());
		else
			return true;
	}

	/**
	 * @deprecated Method getPrimaryElementScope is deprecated
	 */

	public String getPrimaryElementScope(IConfigElement primaryElement)
	{
		return getPrimaryElementScopeEx(primaryElement);
	}

	public static String getPrimaryElementScopeEx(IConfigElement primaryElement)
	{
		IConfigElement scopeElement = primaryElement.getParent();
		if (scopeElement == null || !scopeElement.getName().equals("scope"))
			throw new IllegalArgumentException("getPrimaryElementScope(...) primary element required");
		boolean bPrependSpace = false;
		StringBuffer buf = new StringBuffer(128);
		for (Iterator iterAttributes = scopeElement.getAttributeNames(); iterAttributes.hasNext();)
		{
			String strAttributeName = (String)iterAttributes.next();
			String strAttributeValue = scopeElement.getAttributeValue(strAttributeName);
			if (bPrependSpace)
				buf.append(' ');
			buf.append(strAttributeName).append("='").append(strAttributeValue).append("'");
			bPrependSpace = true;
		}

		return buf.toString();
	}

	public static final String getPrimaryElementVersion(IConfigElement primaryElement)
	{
		return primaryElement.getAttributeValue("version");
	}

	private static void setThreadContext(Context context)
	{
		s_threadContext.setThreadValue(context);
	}

	private static Context getThreadContext()
	{
		return (Context)s_threadContext.getThreadValue();
	}

	private static int incrementReentrentCount()
	{
		int count = 1;
		Integer reentrentCount = (Integer)s_threadReentrantCount.get();
		if (reentrentCount != null)
			count = reentrentCount.intValue() + 1;
		s_threadReentrantCount.set(Integer.valueOf(count));
		return count;
	}

	private static int decrementReentrentCount()
	{
		int count = 0;
		Integer reentrentCount = (Integer)s_threadReentrantCount.get();
		if (reentrentCount != null)
			count = reentrentCount.intValue() - 1;
		s_threadReentrantCount.set(Integer.valueOf(count));
		return count;
	}

	private void traceLookupResult(IConfigElement configElement)
	{
		if (Trace.CONFIGSERVICE)
			if (configElement != null)
			{
				String strValue = configElement.getValue();
				if (strValue != null && strValue.length() != 0)
					Trace.println((new StringBuilder()).append("\telement found (value = '").append(strValue).append("')").toString());
				else
					Trace.println("\telement found");
			} else
			{
				Trace.println("\telement NOT found");
			}
	}

	public static synchronized void removeAllModifications() throws CloneNotSupportedException {
		ConfigService newService;
		ArrayList refreshListeners;
		ConfigService activeService = getInstance();
		HashMap setCfgFiles;
		synchronized (activeService)
		{
			setCfgFiles = (HashMap)activeService.m_mapModificationTargetConfigFiles.clone();
			newService = (ConfigService)activeService.clone();
			s_progressiveSingleton = newService;
		}
		ConfigFile cfgfile;
		for (Iterator i$ = setCfgFiles.values().iterator(); i$.hasNext(); newService.doRemoveConfigFileDOMObject(cfgfile))
			cfgfile = (ConfigFile)i$.next();

		List primaryModifications = new ArrayList();
		int nAppLevel = 0;
		Iterator i$ = newService.m_listConfigFileInfo.iterator();
		do
		{
			if (!i$.hasNext())
				break;
			ConfigFileInfo cfgfileinfo = (ConfigFileInfo)i$.next();
			if (cfgfileinfo.m_nAppLevel != nAppLevel)
			{
				if (primaryModifications.size() > 0)
				{
					newService.processModifications(primaryModifications, nAppLevel);
					primaryModifications.clear();
				}
				nAppLevel = cfgfileinfo.m_nAppLevel;
			}
			String strConfigFileName = cfgfileinfo.m_strFilePath;
			ConfigFile configfileOld = (ConfigFile)setCfgFiles.get(strConfigFileName);
			if (configfileOld != null)
				if (cfgfileinfo.m_fIsAppConfigFile)
				{
					ConfigFile appConfigFile = newService.m_configReader.loadAppConfigFile(cfgfileinfo.m_strAppName);
					newService.initialiseAppConfigFile(appConfigFile, cfgfileinfo.m_strAppName, true);
				} else
				{
					ConfigFile configfileNew = new ConfigFile(configfileOld.getPathName(), null);
					primaryModifications.addAll(newService.initialiseConfigFile(configfileNew, cfgfileinfo.m_strAppName));
				}
		} while (true);
		if (primaryModifications.size() > 0)
		{
			newService.processModifications(primaryModifications, nAppLevel);
			primaryModifications.clear();
		}
		refreshListeners = (ArrayList)s_listRefreshListeners.clone();
		s_isRefreshing = true;
		s_singleton = newService;
		int nCount = refreshListeners.size();
		for (int i = 0; i < nCount; i++)
			((IConfigRefreshListener)refreshListeners.get(i)).onPostRefresh();

		s_isRefreshing = false;
	}

	protected Object clone() throws CloneNotSupportedException {
        ConfigService configClone = (ConfigService) super.clone();
		configClone.m_primaryElementsByLocation = (PrimaryElementsByLocationDictionary)m_primaryElementsByLocation.clone();
		configClone.m_primaryElementsByScope = (PrimaryElementsByScopeDictionary)m_primaryElementsByScope.clone();
		configClone.m_listConfigFileInfo = (ArrayList)m_listConfigFileInfo.clone();
		configClone.m_mapModificationTargetConfigFiles = new HashMap();
		return configClone;
	}

	private void doRemoveConfigFileDOMObject(ConfigFile configFile)
	{
		String strRootFolderPath = m_configReader.getRootFolderPath();
		if (m_appConfigFiles.indexOf(configFile) != -1)
		{
			IConfigElement scopeElement = configFile.getChildElement("scope");
			if (scopeElement == null)
				throw new IllegalStateException("Missing <scope> tag in application file");
			IConfigElement appElement = scopeElement.getChildElement("application");
			if (appElement == null)
				throw new IllegalStateException("Missing <application> tag in application file");
			String strAppName = scopeElement.getAttributeValue("application");
			if (strAppName == null)
				throw new IllegalStateException("Missing attribute for the application name");
			String appElementName = appElement.getName();
			ScopeKey appScopeKey = new ScopeKey((new StringBuilder()).append("application='").append(strAppName).append("'").toString(), m_qualifiers);
			String strAppRelFilePath = configFile.getPathName().substring(strRootFolderPath.length());
			m_primaryElementsByScope.remove(appElementName, appScopeKey);
			m_primaryElementsByLocation.remove(appElementName, strAppRelFilePath);
		} else
		{
			for (Iterator scopeElements = configFile.getChildElements("scope"); scopeElements.hasNext();)
			{
				IConfigElement scopeElement = (IConfigElement)scopeElements.next();
				List scopeKeyList = null;
				Iterator primaryElements = scopeElement.getChildElements();
				while (primaryElements.hasNext()) 
				{
					IConfigElement primaryElement = (IConfigElement)primaryElements.next();
					if (primaryElement.getAttributeValue("modifies") == null)
					{
						if (scopeKeyList == null)
							scopeKeyList = getScopeElementScopeKeys(scopeElement);
						String strPrimaryElementName = getPrimaryElementName(primaryElement);
						PrimaryElementsByScopeDictionary primaryElemByScopeDict = getPrimaryElementsByScope();
						for (int iScopeKey = 0; iScopeKey < scopeKeyList.size(); iScopeKey++)
						{
							ScopeKey scopeKey = (ScopeKey)scopeKeyList.get(iScopeKey);
							primaryElemByScopeDict.remove(strPrimaryElementName, scopeKey);
						}

						String strConfigFilePath = getConfigFilePathName(primaryElement);
						if (strConfigFilePath != null && strConfigFilePath.length() > strRootFolderPath.length())
						{
							strConfigFilePath = strConfigFilePath.substring(strRootFolderPath.length());
							m_primaryElementsByLocation.remove(strPrimaryElementName, strConfigFilePath);
						}
					}
				}
			}

		}
	}

	static void contextualProcess(ContextualProcessor processor, Map context)
	{
		getInstance()._contextualProcess(processor, context);
	}

	private void _contextualProcess(ContextualProcessor processor, Map context)
	{
		Context originalContext;
		boolean conditionalStarted;
		boolean startTrackContext;
		boolean originalAllowClear;
		originalContext = getThreadContext();
		conditionalStarted = false;
		startTrackContext = false;
		originalAllowClear = false;
		conditionalStarted = BoundedContextCache.conditionalStart();
		BoundedContextCache cache = BoundedContextCache.getInstance();
		if (!cache.isContextTracked())
		{
			cache.trackContext(true);
			startTrackContext = true;
		}
		if (cache.getAllowClear())
		{
			cache.setAllowClear(false);
			originalAllowClear = true;
		}
		Context markerContext = new Context();
		String strScopeValues[] = new String[m_qualifiers.length];
		for (int ii = 0; ii < m_qualifiers.length; ii++)
		{
			String scopeName = m_qualifiers[ii].getScopeName();
			strScopeValues[ii] = (String)context.get(scopeName);
		}

		ScopeKey key = new ScopeKey(strScopeValues, m_qualifiers);
		cache.setScopeKey(markerContext, key);
		setThreadContext(markerContext);
		processor.run();
		if (originalAllowClear)
		{
			 cache = BoundedContextCache.getInstance();
			cache.setAllowClear(true);
		}
		if (startTrackContext)
		{
			 cache = BoundedContextCache.getInstance();
			startTrackContext = false;
			cache.trackContext(false);
		}
		if (conditionalStarted)
			BoundedContextCache.end();
		setThreadContext(originalContext);
		if (originalAllowClear)
		{
			 cache = BoundedContextCache.getInstance();
			cache.setAllowClear(true);
		}
		if (startTrackContext)
		{
			 cache = BoundedContextCache.getInstance();
			startTrackContext = false;
			cache.trackContext(false);
		}
		if (conditionalStarted)
			BoundedContextCache.end();
		setThreadContext(originalContext);
	}

	static Preview.Result previewElement(String strElementPath, Map previewContext)
	{
		return getInstance()._previewElement(strElementPath, previewContext);
	}

	private Preview.Result _previewElement(final String strElementPath, Map previewContext)
	{
		if (strElementPath == null || strElementPath.length() == 0)
			throw new IllegalArgumentException("ConfigService.previewElement(...) empty 'strElementPath'");
		final IConfigElement data[] = {
			null, null
		};
		final List tree = new ArrayList();
		ContextualProcessor processor = new ContextualProcessor() {

			final ConfigService this$0;

			public void run()
			{
				String strPrimaryElement = strElementPath;
				String strLesserElements = null;
				int iSeparator = strElementPath.indexOf('.');
				if (iSeparator != -1)
				{
					strPrimaryElement = strElementPath.substring(0, iSeparator);
					strLesserElements = strElementPath.substring(iSeparator + 1);
				}
				IConfigElement primaryElement = m_primaryElementsByScope.get(strPrimaryElement, makeScopeKey(ConfigService.getThreadContext()));
				if (primaryElement != null)
				{
					IConfigElement tempPrimaryElement = primaryElement;
					tree.add(tempPrimaryElement);
					do
					{
						tempPrimaryElement = getExtendedPrimaryElement(tempPrimaryElement);
						if (tempPrimaryElement != null)
							tree.add(tempPrimaryElement);
					} while (tempPrimaryElement != null);
					do
						if (strLesserElements != null)
						{
							data[0] = primaryElement.getDescendantElement(strLesserElements);
							data[1] = primaryElement;
							if (data[0] == null)
							{
								data[1] = null;
								primaryElement = getExtendedPrimaryElement(primaryElement);
							}
						} else
						{
							data[0] = primaryElement;
							data[1] = primaryElement;
						}
					while (data[0] == null && primaryElement != null);
				}
			}

			
			{
				this$0 = ConfigService.this;
			}
		};
		_contextualProcess(processor, previewContext);
		if (data[0] == null && tree.size() == 0)
			return null;
		else
			return new Preview.Result(data[0], data[1], tree);
	}


    public static void main(String[] args){
        Context context = Context.getSessionContext();
        IConfigLookup lookup = ConfigService.getConfigLookup();
        String ss = lookup.lookupString("db.url", context);
        System.out.println(ss);
    }





}
