// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   QualifierParentIterator.java

package com.gihow.dfc;

import java.util.Iterator;
import java.util.LinkedList;

// Referenced classes of package com.documentum.web.formext.config:
//			IQualifier, ScopedDictionary

class QualifierParentIterator
	implements Iterator
{

	private String m_strCurValue;
	private IQualifier m_qualifier;
	private LinkedList m_cachedValues;
	private Iterator m_cacheIter;
	private boolean m_bHasNext;
	private boolean m_bCacheValues;

	QualifierParentIterator(String strInitValue, IQualifier qualifier, boolean bCacheValues)
	{
		m_strCurValue = strInitValue != null && strInitValue.length() != 0 ? strInitValue : ScopedDictionary.STAR;
		m_qualifier = qualifier;
		m_bHasNext = true;
		m_bCacheValues = bCacheValues;
		if (bCacheValues)
		{
			m_cachedValues = new LinkedList();
			m_cachedValues.add(strInitValue);
		}
	}

	public boolean hasNext()
	{
		return m_bHasNext;
	}

	public Object next()
	{
		return nextString();
	}

	public void remove()
	{
	}

	public String nextString()
	{
		String retval = m_strCurValue;
		if (m_strCurValue == null || ScopedDictionary.STAR.equals(m_strCurValue))
		{
			m_bHasNext = false;
			m_strCurValue = null;
		} else
		{
			if (m_bCacheValues && m_cacheIter != null)
			{
				m_strCurValue = (String)(m_cacheIter.hasNext() ? m_cacheIter.next() : null);
				if (m_strCurValue == null)
				{
					m_strCurValue = m_qualifier.getParentScopeValue(retval);
					m_cachedValues.add(m_strCurValue);
					m_cacheIter = null;
				}
			} else
			{
				m_strCurValue = m_qualifier.getParentScopeValue(m_strCurValue);
				if (m_bCacheValues)
					m_cachedValues.add(m_strCurValue);
			}
			if (m_strCurValue == null)
				m_strCurValue = ScopedDictionary.STAR;
		}
		return retval;
	}

	public void reset()
	{
		m_bHasNext = true;
		if (m_bCacheValues)
		{
			m_cacheIter = m_cachedValues.iterator();
			if (m_cacheIter.hasNext())
				m_strCurValue = (String)m_cacheIter.next();
		}
	}
}
