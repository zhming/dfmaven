// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SearchCandidate.java

package com.gihow.dfc;

import java.io.Serializable;
import java.math.BigInteger;

class SearchCandidate
	implements Comparable, Serializable
{

	private int m_index;
	private byte m_relevance[];
	private BigInteger m_relValue;
	boolean m_bValid;

	public SearchCandidate(int index, int nElems)
	{
		m_relValue = null;
		m_index = index;
		m_relevance = new byte[nElems];
		m_bValid = true;
	}

	public void reset()
	{
		m_index = -1;
		m_bValid = true;
		m_relValue = null;
		int len = m_relevance.length;
		for (int i = 0; i < len; i++)
			m_relevance[i] = 0;

	}

	public boolean isValid()
	{
		return m_bValid;
	}

	public void setInvalid()
	{
		m_bValid = false;
	}

	public int getIndex()
	{
		return m_index;
	}

	public void setIndex(int index)
	{
		m_index = index;
	}

	public void setRelevance(int index, int value)
	{
		if (value > 255)
		{
			throw new IllegalArgumentException("Relevance value exceeds 255");
		} else
		{
			m_relevance[index] = (byte)value;
			return;
		}
	}

	public int compareTo(Object o)
	{
		SearchCandidate sc = (SearchCandidate)o;
		return getRelevanceValue().compareTo(sc.getRelevanceValue());
	}

	public boolean equals(Object o)
	{
		if (o instanceof SearchCandidate)
		{
			SearchCandidate sc = (SearchCandidate)o;
			return getRelevanceValue().equals(sc.getRelevanceValue());
		} else
		{
			return false;
		}
	}

	public int hashCode()
	{
		return m_relValue != null ? m_relValue.hashCode() : super.hashCode();
	}

	public String toString()
	{
		StringBuffer b = new StringBuffer(80);
		b.append('{').append("index=").append(m_index).append("; rel=");
		int len = m_relevance.length;
		for (int i = 0; i < len; i++)
			b.append('[').append(m_relevance[i]).append(']');

		b.append("==").append(m_relValue != null ? m_relValue.toString() : "null").append(m_bValid ? " VALID" : " INVALID").append('}');
		return b.toString();
	}

	protected BigInteger getRelevanceValue()
	{
		if (m_relValue == null)
			m_relValue = new BigInteger(1, m_relevance);
		return m_relValue;
	}
}
