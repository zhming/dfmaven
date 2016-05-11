// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ThreadLocalCache.java

package com.gihow.dfc;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Referenced classes of package com.documentum.web.common:
//			Trace

public class ThreadLocalCache
{
	public static interface IResource
	{

		public abstract void cleanup();
	}


	private static final ThreadLocal g_objectCache = new ThreadLocal();

	public static void bindHttpRequest(HttpServletRequest httpservletrequest)
	{
	}

	public static void releaseHttpRequest(HttpServletRequest request)
	{
		clearCache();
	}

	/**
	 * @deprecated Method ThreadLocalCache is deprecated
	 */

	public ThreadLocalCache()
	{
	}

	public static Object get(Object key)
	{
		Map objectIdToObjectMap = getObjectCache(false);
		return objectIdToObjectMap == null ? null : objectIdToObjectMap.get(key);
	}

	public static void put(String key, Object object)
	{
		Map objectIdToObjectMap = getObjectCache();
		objectIdToObjectMap.put(key, object);
	}

	public static void remove(String key)
	{
		Map objectIdToObjectMap = getObjectCache();
		objectIdToObjectMap.remove(key);
	}

	public static boolean containsKey(Object key)
	{
		Map objectIdToObjectMap = getObjectCache();
		return objectIdToObjectMap.containsKey(key);
	}

	private static void clearCache()
	{
		Map objectCache = getObjectCache(false);
		if (objectCache != null)
		{
			Iterator iterator = objectCache.values().iterator();
			do
			{
				if (!iterator.hasNext())
					break;
				Object value = iterator.next();
				if (value != null && (value instanceof IResource))
				{
					IResource resource = (IResource)value;
					try
					{
						resource.cleanup();
					}
					catch (Throwable t)
					{
					}
				}
			} while (true);
		}
		g_objectCache.set(null);
	}

	private static Map getObjectCache()
	{
		return getObjectCache(true);
	}

	private static Map getObjectCache(boolean create)
	{
		Map objectIdToObjectMap = (Map)g_objectCache.get();
		if (create && objectIdToObjectMap == null)
		{
			objectIdToObjectMap = new HashMap();
			g_objectCache.set(objectIdToObjectMap);
		}
		return objectIdToObjectMap;
	}

}
