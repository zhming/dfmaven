// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ScopeKey.java

package com.gihow.dfc;


import com.gihow.util.StringUtils;
import java.util.LinkedHashSet;
import java.util.Set;

// Referenced classes of package com.documentum.web.formext.config:
//			IQualifier, QualifierContext, BoundedContextCache, ScopedDictionary

final class ScopeKey
	implements Comparable
{

	String m_strScopeValues[];
	boolean m_bUndefine;
	IQualifier m_qualifiers[];
	private static final ScopeKey EMPTY_SCOPEKEY_ARRAY[] = new ScopeKey[0];

	private ScopeKey(IQualifier qualifiers[])
	{
		m_bUndefine = false;
		m_qualifiers = qualifiers;
		m_strScopeValues = new String[m_qualifiers.length];
	}

	public ScopeKey(QualifierContext qualifierContext, IQualifier qualifiers[])
	{
		this(qualifiers);
		for (int iQualifier = 0; iQualifier < qualifiers.length; iQualifier++)
		{
			IQualifier qualifier = qualifiers[iQualifier];
			BoundedContextCache cache = BoundedContextCache.getInstance();
			String strScopeValue;
			if (cache != null)
				strScopeValue = cache.retrieveQualifierScopeValue(qualifier, qualifierContext);
			else
				strScopeValue = qualifier.getScopeValue(qualifierContext);
			m_strScopeValues[iQualifier] = strScopeValue != null ? strScopeValue.intern() : null;
		}

	}

	public ScopeKey(String strScopeValues[], IQualifier qualifiers[])
	{
		this(qualifiers);
		boolean bNotOperator = false;
		for (int iQualifier = 0; iQualifier < m_qualifiers.length; iQualifier++)
		{
			String strValue = strScopeValues[iQualifier];
			if (strValue != null && strValue.toLowerCase().startsWith("not "))
			{
				strValue = strValue.substring(3).trim();
				if (bNotOperator)
					throw new IllegalArgumentException("Illegal use of 'not' keyword in scope tag. Occurences are permitted only within a single value.");
				bNotOperator = true;
			}
			m_strScopeValues[iQualifier] = strValue != null ? strValue.intern() : null;
		}

		m_bUndefine = bNotOperator;
	}

	public ScopeKey(String strScopeDesc, IQualifier qualifiers[])
	{
		this(qualifiers);
		if (strScopeDesc.indexOf("application") == -1)
			throw new IllegalStateException((new StringBuilder()).append("ConfigService:  application='...'  is required in scope descriptor: ").append(strScopeDesc).toString());
		for (int iQualifier = 0; iQualifier < m_qualifiers.length; iQualifier++)
		{
			IQualifier qualifier = m_qualifiers[iQualifier];
			String strScopeName = qualifier.getScopeName();
			String strScopeValue = null;
			StringBuffer buf = new StringBuffer(strScopeName.length() + 2);
			buf.append(strScopeName).append("='");
			int iFrom = strScopeDesc.indexOf(buf.toString());
			if (iFrom != -1)
			{
				iFrom += strScopeName.length() + 2;
				int iTo = strScopeDesc.indexOf('\'', iFrom);
				if (iTo != -1)
				{
					strScopeValue = strScopeDesc.substring(iFrom, iTo);
					if (strScopeValue.equals("*"))
						strScopeValue = null;
				}
			}
			m_strScopeValues[iQualifier] = strScopeValue != null ? strScopeValue.intern() : null;
		}

	}

	public ScopeKey(ScopeKey scopeKey)
	{
		this(scopeKey.m_qualifiers);
		m_strScopeValues = new String[scopeKey.m_strScopeValues.length];
		for (int i = 0; i < m_strScopeValues.length; i++)
			m_strScopeValues[i] = scopeKey.m_strScopeValues[i];

	}

	public boolean isUndefined()
	{
		return m_bUndefine;
	}

	public String getDesc()
	{
		StringBuffer scopeDesc = new StringBuffer(80);
		for (int iQualifierKey = 0; iQualifierKey < m_qualifiers.length; iQualifierKey++)
		{
			String strScopeItem = m_strScopeValues[iQualifierKey];
			if (strScopeItem == null)
				continue;
			if (scopeDesc.length() > 0)
				scopeDesc.append(", ");
			scopeDesc.append(m_qualifiers[iQualifierKey].getScopeName());
			scopeDesc.append("='");
			scopeDesc.append(strScopeItem);
			scopeDesc.append("'");
		}

		return scopeDesc.toString();
	}

	public String toString()
	{
		int iQualifiersLength = m_qualifiers.length;
		int iBufferLength = 0;
		for (int iQualifierKey = 0; iQualifierKey < iQualifiersLength; iQualifierKey++)
			if (m_strScopeValues[iQualifierKey] != null)
				iBufferLength += m_strScopeValues[iQualifierKey].length();
			else
				iBufferLength += 4;

		StringBuffer scopeKey = new StringBuffer(iQualifiersLength * 12 + iBufferLength);
		for (int iQualifierKey = 0; iQualifierKey < iQualifiersLength; iQualifierKey++)
		{
			if (iQualifierKey > 0)
				scopeKey.append(", ");
			scopeKey.append(m_qualifiers[iQualifierKey].getScopeName());
			scopeKey.append("='");
			String strScopeItem = m_strScopeValues[iQualifierKey];
			if (strScopeItem != null)
				scopeKey.append(strScopeItem);
			else
				scopeKey.append('*');
			scopeKey.append('\'');
		}

		return scopeKey.toString();
	}

	public int compareTo(Object o)
	{
		int retval = 0;
		ScopeKey key = (ScopeKey)o;
		String oVals[] = key.m_strScopeValues;
		for (int i = 0; retval == 0 && i < m_strScopeValues.length; i++)
		{
			String s = m_strScopeValues[i];
			if (s == null || ScopedDictionary.STAR.equals(s))
			{
				retval = oVals[i] != null && !ScopedDictionary.STAR.equals(oVals[i]) ? -1 : 0;
			} else
			{
				String oStr = oVals[i] != null ? oVals[i] : ScopedDictionary.STAR;
				retval = m_strScopeValues[i].compareTo(oStr);
			}
		}

		return retval;
	}

	public boolean equals(Object o)
	{
		boolean retval = false;
		if (o instanceof ScopeKey)
		{
			ScopeKey key = (ScopeKey)o;
			int len = m_strScopeValues.length;
			if (key != null && key.m_strScopeValues != null && (retval = len == key.m_strScopeValues.length))
			{
				for (int i = 0; retval && i < len; i++)
				{
					String sThis = m_strScopeValues[i];
					String sThat = key.m_strScopeValues[i];
					if (sThis == null || ScopedDictionary.STAR.equals(sThis))
						retval = sThat == null || ScopedDictionary.STAR.equals(sThat);
					else
						retval = m_strScopeValues[i] == key.m_strScopeValues[i];
				}

			}
		}
		return retval;
	}

	public int hashCode()
	{
		int len = m_strScopeValues.length;
		int i = 0;
		long hc = 0L;
		for (i = 0; i < len; i++)
		{
			String s = m_strScopeValues[i];
			hc += s != null && !ScopedDictionary.STAR.equals(s) ? s.hashCode() : i;
		}

		return (int)(-1L & hc);
	}

	private ScopeKey changeScopeValue(ScopeKey scopeKey, int scopeValueIndex, String sourceStr, String replaceStr)
	{
		scopeKey.m_strScopeValues[scopeValueIndex] = StringUtils.replace(scopeKey.m_strScopeValues[scopeValueIndex], sourceStr, replaceStr);
		return scopeKey;
	}

	ScopeKey[] split()
	{
		Set multiKeys = null;
		for (int i = 0; i < m_strScopeValues.length; i++)
		{
			boolean scopeValueContainsComma = false;
			if (m_strScopeValues[i] == null || m_strScopeValues[i].indexOf(",") == -1)
				continue;
			if (multiKeys == null)
				multiKeys = new LinkedHashSet();
			String scopeValue = m_strScopeValues[i];
			if (m_strScopeValues[i].indexOf(",,") != -1)
			{
				scopeValue = StringUtils.replace(m_strScopeValues[i], ",,", "(}]");
				scopeValueContainsComma = true;
			}
			String keys[] = scopeValue.split(",");
label0:
			for (int j = 0; j < keys.length; j++)
			{
				ScopeKey newScopeKey = new ScopeKey(this);
				newScopeKey.setScopeValue(i, keys[j]);
				if (multiKeys.contains(newScopeKey))
					continue;
				ScopeKey o[] = newScopeKey.split();
				if (o == null)
				{
					if (scopeValueContainsComma)
						newScopeKey = changeScopeValue(newScopeKey, i, "(}]", ",");
					multiKeys.add(newScopeKey);
					continue;
				}
				int k = 0;
				do
				{
					if (k >= o.length)
						continue label0;
					if (scopeValueContainsComma)
						o[k] = changeScopeValue(o[k], i, "(}]", ",");
					multiKeys.add(o[k]);
					k++;
				} while (true);
			}

			if (multiKeys.size() > 0)
				break;
		}

		if (multiKeys != null)
			return (ScopeKey[])(ScopeKey[])multiKeys.toArray(EMPTY_SCOPEKEY_ARRAY);
		else
			return null;
	}

	private void setScopeValue(int newScopeValueIndex, String newScopeValue)
	{
		m_strScopeValues[newScopeValueIndex] = newScopeValue == null ? null : newScopeValue.intern();
	}

	public static void main(String args[])
	{
		String scopeValues[][] = {
			{
				"dell", "null", "none", "acm_business_admin_role,ecm_sec_users_domain_role,consumer", "webbrowser", "custom", "latest", "null", "rps,collaboration,recordsmanager"
			}, {
				"dell", "creategroup", "acm_business_admin_role,ecm legal coordinator role,ecm debug role,ecm legal consumer role,ecm legal contributor role,ecm legal report role,ecm_sec_users_domain_role,contributor,consumer", "webbrowser", "custom", "latest", "collaboration"
			}, {
				"a,b,c", "d", "e,f"
			}, {
				"a", "d", "e"
			}, {
				"a,b,c", "d", "e"
			}, {
				"a", "d,e,f", "e"
			}, {
				"a,b,c", "d,e,f", "g,h,i"
			}
		};
		for (int i = 0; i < scopeValues.length; i++)
		{
			ScopeKey scopeKey = new ScopeKey(scopeValues[i], new IQualifier[scopeValues[i].length]);
			ScopeKey r[] = scopeKey.split();
			System.out.print((new StringBuilder()).append("input: ").append(getArrayAsString(scopeValues[i])).append("\noutput: ").toString());
			if (r != null)
			{
				for (int j = 0; j < r.length; j++)
				{
					ScopeKey key = r[j];
					System.out.println((new StringBuilder()).append("\t{").append(getArrayAsString(key.m_strScopeValues)).append("}").toString());
				}

			}
			System.out.println();
		}

	}

	private static String getArrayAsString(String scopeValue[])
	{
		StringBuffer s = new StringBuffer("");
		for (int i = 0; i < scopeValue.length; i++)
			s.append(",[").append(scopeValue[i]).append("]");

		return s.substring(1);
	}

}
