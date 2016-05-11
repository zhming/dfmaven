// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ScopedDictionary.java

package com.gihow.dfc;

import java.math.BigInteger;
import java.util.*;

// Referenced classes of package com.documentum.web.formext.config:
//			SearchCandidate, ScopedDictionaryEntry, ConfigKey, QualifierParentIterator, 
//			ScopeKey, IQualifier

class ScopedDictionary
	implements Cloneable
{
	class StringIndexPair
		implements Comparable
	{

		public String m_strPrimElem;
		public int m_index;
		final ScopedDictionary this$0;

		public int compareTo(Object o)
		{
			StringIndexPair p = (StringIndexPair)o;
			int retval;
			if (m_strPrimElem == null)
				retval = p.m_strPrimElem != null ? -1 : 0;
			else
				retval = m_strPrimElem.compareTo(p.m_strPrimElem);
			if (retval == 0)
				if (m_index < p.m_index)
					retval = -1;
				else
				if (m_index > p.m_index)
					retval = 1;
			return retval;
		}

		public boolean equals(Object o)
		{
			if (o instanceof StringIndexPair)
			{
				StringIndexPair p = (StringIndexPair)o;
				return m_index == p.m_index && primElemsEqual(p.m_strPrimElem);
			} else
			{
				return false;
			}
		}

		protected boolean primElemsEqual(String strOtherPrimElem)
		{
			if (m_strPrimElem == null)
				return strOtherPrimElem == null;
			else
				return m_strPrimElem.equals(strOtherPrimElem);
		}

		public int hashCode()
		{
			int retval = m_strPrimElem != null ? m_strPrimElem.hashCode() : 0;
			return retval <= 0 ? retval + m_index : retval - m_index;
		}

		public String toString()
		{
			return (new StringBuilder()).append(m_strPrimElem).append("::").append(m_index).toString();
		}

		public StringIndexPair()
		{
            super();
			this$0 = ScopedDictionary.this;

			m_strPrimElem = "";
			m_index = 0;
		}

		public StringIndexPair(String strPrimElem, int index)
		{
            super();
			this$0 = ScopedDictionary.this;

			m_strPrimElem = strPrimElem;
			m_index = index;
		}
	}


	private IQualifier m_qualifiers[];
	private HashMap m_hashElems;
	private HashMap m_hashLookup;
	private HashMap m_primElemToHashLookup;
	private TreeSet m_setPrimElems;
	private ArrayList m_configEntries;
	private int m_nNextIndex;
	public static final String STAR = "*".intern();
	public static final Object NULL_SENTINEL = new Object();

	public ScopedDictionary(int initSize)
	{
		m_hashElems = new HashMap(initSize);
		m_hashLookup = new HashMap(initSize);
		m_primElemToHashLookup = new HashMap(initSize);
		m_setPrimElems = new TreeSet();
		m_configEntries = new ArrayList(initSize);
		m_nNextIndex = 0;
	}

	public Object get(ConfigKey key)
	{
		Object retval = m_hashLookup.get(key);
		if (retval == null && key != null)
		{
			ConfigKey multiKeys[] = key.split();
			if (multiKeys == null || multiKeys.length == 1)
			{
				if (multiKeys != null && multiKeys.length == 1)
					key = multiKeys[0];
				retval = getMostRelevant(key);
			} else
			{
				retval = getMostRelevantForMultiKeys(multiKeys);
			}
			if (retval != null)
				m_hashLookup.put(key, retval);
			else
				m_hashLookup.put(key, NULL_SENTINEL);
			trackCache(key);
		}
		return retval;
	}

	private Object getMostRelevantForMultiKeys(ConfigKey multiKeys[])
	{
		List searchCandidates = new ArrayList();
		for (int i = 0; i < multiKeys.length; i++)
		{
			ConfigKey key = multiKeys[i];
			SearchCandidate mostRel = getBestSearchCandidate(key);
			if (mostRel != null && mostRel.isValid())
				searchCandidates.add(mostRel);
		}

		Object retval = null;
		if (searchCandidates.size() > 0)
		{
			Collections.sort(searchCandidates);
			SearchCandidate mostRel = (SearchCandidate)searchCandidates.get(0);
			retval = ((ScopedDictionaryEntry)m_configEntries.get(mostRel.getIndex())).m_configElem;
		}
		return retval;
	}

	public Object getExact(ConfigKey key)
	{
		return m_hashElems.get(key);
	}

	public void remove(ConfigKey key)
	{
		clearCache(key.getPrimaryElem());
		m_hashElems.remove(key);
		int index = getExactIndex(key);
		if (index >= 0)
		{
			m_setPrimElems.remove(new StringIndexPair(key.m_primElem, index));
			m_configEntries.set(index, new ScopedDictionaryEntry(key, NULL_SENTINEL));
		}
	}

	public void put(ConfigKey key, Object configElem)
	{
		m_hashElems.put(key, configElem);
		m_hashLookup.put(key, configElem);
		m_configEntries.add(new ScopedDictionaryEntry(key, configElem));
		m_setPrimElems.add(new StringIndexPair(key.m_primElem, m_nNextIndex));
		m_nNextIndex++;
	}

	public Set keySet()
	{
		return m_hashElems.keySet();
	}

	public int size()
	{
		return m_hashElems.size();
	}

	public void setQualifiers(IQualifier qualifiers[])
	{
		m_qualifiers = qualifiers;
	}

	private void trackCache(ConfigKey key)
	{
		String primaryElement = key.getPrimaryElem();
		if (m_primElemToHashLookup.containsKey(primaryElement))
		{
			((ArrayList)m_primElemToHashLookup.get(primaryElement)).add(key);
		} else
		{
			ArrayList cachedLookupKeys = new ArrayList(4);
			cachedLookupKeys.add(key);
			m_primElemToHashLookup.put(primaryElement, cachedLookupKeys);
		}
	}

	private void clearCache(String primaryElement)
	{
		ArrayList cachedLookupKeys = (ArrayList)m_primElemToHashLookup.get(primaryElement);
		if (cachedLookupKeys != null)
		{
			ConfigKey cacheLookupKey;
			for (Iterator iter = cachedLookupKeys.iterator(); iter.hasNext(); m_hashLookup.remove(cacheLookupKey))
				cacheLookupKey = (ConfigKey)iter.next();

			m_primElemToHashLookup.remove(primaryElement);
		}
	}

	protected Object getMostRelevant(ConfigKey key)
	{
		Object retval = null;
		int index = getMostRelevantIndex(key);
		if (index >= 0)
			retval = ((ScopedDictionaryEntry)m_configEntries.get(index)).m_configElem;
		return retval;
	}

	protected int getMostRelevantIndex(ConfigKey key)
	{
		int retval = -1;
		SearchCandidate mostRel = getBestSearchCandidate(key);
		if (mostRel != null)
			retval = mostRel.getIndex();
		return retval;
	}

	private int getExactIndex(ConfigKey key)
	{
		if (key == null)
			return -1;
		String strPrimElem = key.m_primElem;
		StringIndexPair start = new StringIndexPair(strPrimElem, 0);
		StringIndexPair endPlusOne = new StringIndexPair(strPrimElem, 0x7fffffff);
		SortedSet set = m_setPrimElems.subSet(start, endPlusOne);
		for (Iterator it = set.iterator(); it.hasNext();)
		{
			StringIndexPair p = (StringIndexPair)it.next();
			ScopedDictionaryEntry entry = (ScopedDictionaryEntry)m_configEntries.get(p.m_index);
			if (key.equals(entry.getKey()))
				return p.m_index;
		}

		return -1;
	}

	protected SearchCandidate getBestSearchCandidate(ConfigKey key)
	{
		SearchCandidate retval = null;
		String strPrimElem = key.m_primElem;
		StringIndexPair start = new StringIndexPair(strPrimElem, 0);
		StringIndexPair endPlusOne = new StringIndexPair(strPrimElem, 0x7fffffff);
		SortedSet set = m_setPrimElems.subSet(start, endPlusOne);
		int qualSize = m_qualifiers.length;
		boolean bCacheValues = set.size() > 1;
		QualifierParentIterator qualIters[] = new QualifierParentIterator[qualSize];
		for (int i = 0; i < qualSize; i++)
			qualIters[i] = new QualifierParentIterator(key.m_scopeKey.m_strScopeValues[i], m_qualifiers[i], bCacheValues);

		SearchCandidate candidate = null;
		Iterator it = set.iterator();
		do
		{
			if (!it.hasNext())
				break;
			StringIndexPair p = (StringIndexPair)it.next();
			ScopedDictionaryEntry entry = (ScopedDictionaryEntry)m_configEntries.get(p.m_index);
			if (candidate == null)
			{
				candidate = new SearchCandidate(p.m_index, qualSize);
			} else
			{
				candidate.reset();
				candidate.setIndex(p.m_index);
			}
			entry.calculateColumnMatches(qualIters, candidate);
			if (candidate.m_bValid)
			{
				if (candidate.getRelevanceValue().equals(BigInteger.ZERO))
					return candidate;
				if (retval == null)
				{
					retval = candidate;
					candidate = null;
				} else
				if (candidate.compareTo(retval) < 0)
				{
					SearchCandidate temp = retval;
					retval = candidate;
					candidate = temp;
				}
			}
			if (bCacheValues)
			{
				int i = 0;
				while (i < qualSize) 
				{
					qualIters[i].reset();
					i++;
				}
			}
		} while (true);
		return retval;
	}

	protected Object clone()
	{
		ScopedDictionary clone = null;
        try {
            clone = (ScopedDictionary)super.clone();
            clone.m_hashElems = (HashMap)m_hashElems.clone();
            clone.m_hashLookup = (HashMap)m_hashLookup.clone();
            clone.m_primElemToHashLookup = (HashMap)m_primElemToHashLookup.clone();
            clone.m_setPrimElems = (TreeSet)m_setPrimElems.clone();
            clone.m_configEntries = (ArrayList)m_configEntries.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

		return clone;
	}

}
