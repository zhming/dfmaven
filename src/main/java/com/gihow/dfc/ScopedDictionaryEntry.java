// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ScopedDictionaryEntry.java

package com.gihow.dfc;


// Referenced classes of package com.documentum.web.formext.config:
//			ConfigKey, ScopeKey, ScopedDictionary, SearchCandidate, 
//			QualifierParentIterator

class ScopedDictionaryEntry
{

	ConfigKey m_key;
	Object m_configElem;

	public ScopedDictionaryEntry(ConfigKey key, Object configElem)
	{
		m_key = key;
		m_configElem = configElem;
	}

	public ConfigKey getKey()
	{
		return m_key;
	}

	public Object getConfigElement()
	{
		return m_configElem;
	}

	public void calculateColumnMatches(QualifierParentIterator qualIters[], SearchCandidate searchCand)
	{
		int len = qualIters.length;
		for (int i = 0; i < len && calculateColumnMatch(i, qualIters[i], searchCand); i++);
	}

	protected boolean calculateColumnMatch(int scopeIndex, QualifierParentIterator it, SearchCandidate searchCand)
	{
		boolean found = false;
		String strIndexScopeVal = m_key.m_scopeKey.m_strScopeValues[scopeIndex];
		if (strIndexScopeVal == null || ScopedDictionary.STAR.equals(strIndexScopeVal))
		{
			searchCand.setRelevance(scopeIndex, 255);
			found = true;
		} else
		{
			for (int n = 0; it.hasNext() && !found; n++)
			{
				String s = it.nextString();
				if (strIndexScopeVal.equals(s))
				{
					found = true;
					searchCand.setRelevance(scopeIndex, n);
				}
			}

		}
		if (!found)
			searchCand.setInvalid();
		return found;
	}
}
