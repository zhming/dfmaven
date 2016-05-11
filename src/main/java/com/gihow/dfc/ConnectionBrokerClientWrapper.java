// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ConnectionBrokerClientWrapper.java

package com.gihow.dfc;

import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfDocbrokerClient;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

class ConnectionBrokerClientWrapper
	implements InvocationHandler
{
	private static class DocbaseMapCacheUtil
	{
		private static class DocbaseMapWrapper
			implements Serializable
		{

			private transient IDfDocbaseMap m_docbaseMap;

			public IDfDocbaseMap getDocbaseMap()
			{
				return m_docbaseMap;
			}

			public DocbaseMapWrapper(IDfDocbaseMap docbaseMap)
			{
				m_docbaseMap = null;
				m_docbaseMap = docbaseMap;
			}
		}


		private static final String DEFAULT_DOCBASE_MAP_CACHE = "DefaultDocbaseMapCache";
		private static final String SESSION_STATE_DOCBASE_MAP_CACHE_KEY = "DocbaseMapsCache";
		private static final String DEFAULT_DOCBROKER_PORT = "1489";

		public static IDfDocbaseMap getDocbaseMapFromSpecificDocbroker(IDfDocbrokerClient client, String strProtocol, String strDocbroker, String strPortNumber)
			throws DfException
		{
			IDfDocbaseMap map = null;
			if (strDocbroker == null)
				return map;
			if (strPortNumber == null || strPortNumber.trim().length() <= 0)
				strPortNumber = "1489";
			Map docbaseMaps = (Map)SessionState.getAttribute("DocbaseMapsCache");
			if (docbaseMaps == null)
			{
				docbaseMaps = new HashMap();
				SessionState.setAttribute("DocbaseMapsCache", docbaseMaps);
			}
			String strKey = getCacheKey(strProtocol, strDocbroker, strPortNumber);
			if (docbaseMaps.containsKey(strKey))
			{
				DocbaseMapWrapper wrapper = (DocbaseMapWrapper)docbaseMaps.get(strKey);
				map = wrapper.getDocbaseMap();
			}
			if (map == null)
			{
				if (strDocbroker.equals("DefaultDocbaseMapCache"))
					map = client.getDocbaseMap();
				else
					map = client.getDocbaseMapFromSpecificDocbroker(strProtocol, strDocbroker, strPortNumber);
				docbaseMaps.put(strKey, new DocbaseMapWrapper(map));
			}
			return map;
		}

		public static IDfDocbaseMap getDocbaseMap(IDfDocbrokerClient client)
			throws DfException
		{
			return getDocbaseMapFromSpecificDocbroker(client, null, "DefaultDocbaseMapCache", "1489");
		}

		public static void clearDocbaseMaps()
		{
			SessionState.removeAttribute("DocbaseMapsCache");
		}

		private static String getCacheKey(String strProtocol, String strDocbroker, String strPortNumber)
		{
			StringBuffer strBuf = new StringBuffer();
			if (strProtocol != null)
				strBuf.append(strProtocol).append("-");
			strBuf.append(strDocbroker).append("-").append(strPortNumber).append("-Map");
			return strBuf.toString();
		}

		private DocbaseMapCacheUtil()
		{
		}
	}


	private IDfDocbrokerClient m_docbrokerClient;
	private static final String SESSION_STATE_DOCBASE_ID_TO_NAME_CACHE_KEY = "DocbaseIdToNameCache";

	private ConnectionBrokerClientWrapper(IDfDocbrokerClient docbrokerClient)
	{
		m_docbrokerClient = null;
		m_docbrokerClient = docbrokerClient;
	}

	static IDfDocbrokerClient getInstance(IDfDocbrokerClient docbrokerClient)
	{
		IDfDocbrokerClient wrapper = (IDfDocbrokerClient)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] {
			com.documentum.fc.client.IDfDocbrokerClient.class
		}, new ConnectionBrokerClientWrapper(docbrokerClient));
		return wrapper;
	}

	public Object invoke(Object proxy, Method method, Object args[])
		throws Throwable
	{
		Object obj = null;
		try
		{
			String name = method.getName();
			if (name.equals("addDocbroker") || name.equals("removeDocbroker"))
			{
				DocbaseMapCacheUtil.clearDocbaseMaps();
				obj = method.invoke(m_docbrokerClient, args);
			} else
			if (name.equals("getDocbaseMap") && (args == null || args.length == 0))
				obj = DocbaseMapCacheUtil.getDocbaseMap(m_docbrokerClient);
			else
			if (name.equals("getDocbaseMapFromSpecificDocbroker") && args != null && args.length == 3)
				obj = DocbaseMapCacheUtil.getDocbaseMapFromSpecificDocbroker(m_docbrokerClient, (String)args[0], (String)args[1], (String)args[2]);
			else
			if (name.equals("getDocbaseNameFromId") && args != null && args.length == 1)
				obj = getDocbaseNameFromId(m_docbrokerClient, (IDfId)args[0]);
			else
				obj = method.invoke(m_docbrokerClient, args);
		}
		catch (InvocationTargetException e)
		{
			throw e.getTargetException();
		}
		return obj;
	}

	private static String getDocbaseNameFromId(IDfDocbrokerClient client, IDfId objId)
		throws DfException
	{
		String docbaseName = null;
		if (objId == null || !objId.isObjectId())
			return docbaseName;
		Map docbaseIdMap = (Map)SessionState.getAttribute("DocbaseIdToNameCache");
		if (docbaseIdMap == null)
		{
			docbaseIdMap = new HashMap();
			SessionState.setAttribute("DocbaseIdToNameCache", docbaseIdMap);
		}
		String strKey = objId.getDocbaseId();
		if (docbaseIdMap.containsKey(strKey))
		{
			docbaseName = (String)docbaseIdMap.get(strKey);
		} else
		{
			docbaseName = client.getDocbaseNameFromId(objId);
			docbaseIdMap.put(strKey, docbaseName);
		}
		return docbaseName;
	}
}
