// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ArgumentList.java

package com.gihow.dfc;

import com.gihow.util.StringUtil;

import java.io.Serializable;
import java.util.*;

public final class ArgumentList
	implements Serializable
{

	private HashMap m_hashValues;
	private ArrayList m_arrOrderedNames;
	private static final String ENCODER_DELIMITER_EQUALS = "~";
	private static final String ENCODER_DELIMITER_COMMA = "|";
	private static final String ENCODER_DELIMITER_ESC = "\\";
	private static final String ENCODER_SPECIAL_CHARS = "\\|~'";

	public ArgumentList()
	{
		m_hashValues = new HashMap(5, 1.0F);
		m_arrOrderedNames = new ArrayList(4);
	}

	public ArgumentList(ArgumentList args)
	{
		m_hashValues = new HashMap(5, 1.0F);
		m_arrOrderedNames = new ArrayList(4);
		add(args);
	}

	public String get(String strArgName)
	{
		return get(strArgName, 0);
	}

	public String get(String strArgName, int nValueIndex)
	{
		String strValue = null;
		Object oValue = m_hashValues.get(strArgName);
		if (oValue != null)
			if (oValue instanceof ArrayList)
			{
				ArrayList arrValues = (ArrayList)oValue;
				if (nValueIndex >= 0 && nValueIndex < arrValues.size())
					strValue = (String)arrValues.get(nValueIndex);
			} else
			if (nValueIndex == 0)
				strValue = (String)oValue;
		return strValue;
	}

	public String[] getValues(String strArgName)
	{
		String strValues[] = null;
		Object oValue = m_hashValues.get(strArgName);
		if (oValue != null)
			if (oValue instanceof ArrayList)
			{
				ArrayList arrValues = (ArrayList)oValue;
				strValues = new String[arrValues.size()];
				arrValues.toArray(strValues);
			} else
			{
				strValues = (new String[] {
					(String)oValue
				});
			}
		return strValues;
	}

	public boolean hasMultipleValues(String strArgName)
	{
		boolean bHasMultipleValues = false;
		Object oValue = m_hashValues.get(strArgName);
		if (oValue != null)
			bHasMultipleValues = oValue instanceof ArrayList;
		return bHasMultipleValues;
	}

	public ArgumentList add(String strArgName, String strArgValue)
	{
		Object oValue = m_hashValues.get(strArgName);
		if (oValue == null)
		{
			m_hashValues.put(strArgName, strArgValue);
			m_arrOrderedNames.add(strArgName);
		} else
		if (oValue instanceof ArrayList)
		{
			ArrayList arrValues = (ArrayList)oValue;
			arrValues.add(strArgValue);
		} else
		{
			ArrayList arrValues = new ArrayList(4);
			arrValues.add(oValue);
			arrValues.add(strArgValue);
			m_hashValues.put(strArgName, arrValues);
		}
		return this;
	}

	public ArgumentList add(String strArgName, String strArgValues[])
	{
		for (int i = 0; i < strArgValues.length; i++)
			add(strArgName, strArgValues[i]);

		return this;
	}

	public ArgumentList add(ArgumentList args)
	{
		if (args != null)
		{
			Iterator iterNames = args.nameIterator();
			do
			{
				if (!iterNames.hasNext())
					break;
				String strName = (String)iterNames.next();
				String strValues[] = args.getValues(strName);
				if (strValues != null)
				{
					int iValue = 0;
					while (iValue < strValues.length) 
					{
						add(strName, strValues[iValue]);
						iValue++;
					}
				}
			} while (true);
		}
		return this;
	}

	public void replace(String strArgName, String strArgValue)
	{
		remove(strArgName);
		add(strArgName, strArgValue);
	}

	public void remove(String strArgName)
	{
		m_hashValues.remove(strArgName);
		m_arrOrderedNames.remove(strArgName);
	}

	public Iterator nameIterator()
	{
		return m_arrOrderedNames.iterator();
	}

	public List nameList()
	{
		return m_arrOrderedNames;
	}

	public boolean isEmpty()
	{
		return m_arrOrderedNames.isEmpty();
	}

	public String toString()
	{
		StringBuffer strBuf = new StringBuffer(256);
		strBuf.append('{');
		boolean bFirst = true;
		Iterator iterName = nameIterator();
		do
		{
			if (!iterName.hasNext())
				break;
			String strName = (String)iterName.next();
			String strValues[] = getValues(strName);
			if (strValues != null)
			{
				int iArgValue = 0;
				while (iArgValue < strValues.length) 
				{
					if (!bFirst)
						strBuf.append(',');
					strBuf.append(strName);
					strBuf.append('=');
					strBuf.append(strValues[iArgValue]);
					bFirst = false;
					iArgValue++;
				}
			}
		} while (true);
		strBuf.append('}');
		return strBuf.toString();
	}

	public static String encode(ArgumentList args)
	{
		StringBuffer strArgs = new StringBuffer(128);
		boolean bAppendComma = false;
		for (Iterator argNames = args.nameIterator(); argNames.hasNext();)
		{
			String strArgName = (String)argNames.next();
			String strArgValues[] = args.getValues(strArgName);
			String strEscapedArgName = escape(strArgName);
			int iArgValue = 0;
			while (iArgValue < strArgValues.length) 
			{
				if (bAppendComma)
					strArgs.append("|");
				String strArgValue = strArgValues[iArgValue];
				strArgs.append(strEscapedArgName);
				strArgs.append("~");
				strArgs.append(escape(strArgValue));
				bAppendComma = true;
				iArgValue++;
			}
		}

		return strArgs.toString();
	}

	public static ArgumentList decode(String strArgs)
	{
		ArgumentList args = new ArgumentList();
		String strName;
		String strValue;
		for (Iterator itArgs = splitEscapedString(strArgs, "|".charAt(0)).iterator(); itArgs.hasNext(); args.add(strName, strValue))
		{
			String strToken = (String)itArgs.next();
			Vector vecNameValue = splitEscapedString(strToken, "~".charAt(0));
			strName = unescape((String)vecNameValue.elementAt(0));
			strValue = "";
			if (vecNameValue.size() > 1)
				strValue = unescape((String)vecNameValue.elementAt(1));
		}

		return args;
	}

	private static Vector splitEscapedString(String strParameters, char chDelimiter)
	{
		Vector vecParameters = new Vector();
		char chEscape = "\\".charAt(0);
		if (strParameters.indexOf(chEscape) == -1)
		{
			vecParameters = StringUtil.splitString(strParameters, Character.toString(chDelimiter));
		} else
		{
			int nDelimPos = strParameters.indexOf(chDelimiter);
			if (nDelimPos == -1)
			{
				vecParameters.add(strParameters);
			} else
			{
				int nStartPos = 0;
				do
				{
					if (nDelimPos == -1)
						break;
					for (; nDelimPos <= strParameters.length() - 1 && nDelimPos > 0 && strParameters.charAt(nDelimPos - 1) == chEscape && strParameters.charAt(nDelimPos - 2) != chEscape; nDelimPos = strParameters.indexOf(chDelimiter, nDelimPos + 1));
					if (nDelimPos != -1)
					{
						String strToken = strParameters.substring(nStartPos, nDelimPos);
						vecParameters.addElement(strToken);
						nStartPos = nDelimPos + 1;
						nDelimPos = strParameters.indexOf(chDelimiter, nStartPos);
						if (nDelimPos == -1)
						{
							strToken = strParameters.substring(nStartPos);
							vecParameters.add(strToken);
						}
					} else
					{
						String strToken = strParameters.substring(nStartPos);
						nDelimPos = -1;
						vecParameters.addElement(strToken);
					}
				} while (true);
			}
		}
		return vecParameters;
	}

	private static String escape(String strParameters)
	{
		boolean fNeedEscaping = false;
		int idxChar = 0;
		do
		{
			if (idxChar >= "\\|~'".length())
				break;
			if (strParameters.indexOf("\\|~'".charAt(idxChar)) != -1)
			{
				fNeedEscaping = true;
				break;
			}
			idxChar++;
		} while (true);
		if (fNeedEscaping)
		{
			int nTextLen = strParameters.length();
			StringBuffer strEscaped = new StringBuffer(nTextLen + 10);
			for (int iTextIdx = 0; iTextIdx < nTextLen; iTextIdx++)
			{
				char ch = strParameters.charAt(iTextIdx);
				if ("\\|~'".indexOf(ch) != -1)
					strEscaped.append("\\");
				strEscaped.append(ch);
			}

			strParameters = strEscaped.toString();
		}
		return strParameters;
	}

	private static String unescape(String strParameters)
	{
		char chEscape = "\\".charAt(0);
		if (strParameters.indexOf(chEscape) != -1)
		{
			int nTextLen = strParameters.length();
			StringBuffer strUnescaped = new StringBuffer(nTextLen + 10);
			for (int iTextIdx = 0; iTextIdx < nTextLen; iTextIdx++)
			{
				char ch = strParameters.charAt(iTextIdx);
				if (ch == chEscape && iTextIdx < nTextLen - 1 && "\\|~'".indexOf(strParameters.charAt(iTextIdx + 1)) != -1)
				{
					ch = strParameters.charAt(iTextIdx + 1);
					iTextIdx++;
				}
				strUnescaped.append(ch);
			}

			strParameters = strUnescaped.toString();
		}
		return strParameters;
	}
}
