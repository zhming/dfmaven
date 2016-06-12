// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StringUtil.java

package com.ecm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// Referenced classes of package com.documentum.web.util:

public final class StringUtil
{

	static final char s_digits[] = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 
		'u', 'v', 'w', 'x', 'y', 'z'
	};
	public static final String COMMA_ENCODER = "#~@";

	public StringUtil()
	{
	}

	public static String replaceAll(String strSource, char oldc, char newc)
	{
		StringBuffer bufReplacement = new StringBuffer(128);
		for (int i = 0; i < strSource.length(); i++)
			if (strSource.charAt(i) == oldc)
				bufReplacement.append(newc);
			else
				bufReplacement.append(strSource.charAt(i));

		return bufReplacement.toString();
	}

	public static String replace(String strSource, char chars[], char newChar)
	{
		StringBuffer bufReplacement = new StringBuffer(strSource);
		for (int i = 0; i < bufReplacement.length(); i++)
		{
			for (int k = 0; k < chars.length; k++)
				if (bufReplacement.charAt(i) == chars[k])
					bufReplacement.setCharAt(i, newChar);

		}

		return bufReplacement.toString();
	}

	public static String replace(String strSource, String strSearch, String strReplace)
	{
		if (strSource == null || strSearch == null || strReplace == null)
			throw new IllegalArgumentException("StringUtil.replace(...) null paramater passed");
		int iTo = strSource.indexOf(strSearch);
		if (iTo == -1)
			return strSource;
		StringBuffer buf = new StringBuffer(strSource.length());
		int searchLen = strSearch.length();
		int iFrom = 0;
		do
		{
			if (iTo > iFrom)
				buf.append(strSource.substring(iFrom, iTo));
			iFrom = iTo + searchLen;
			buf.append(strReplace);
			iTo = strSource.indexOf(strSearch, iFrom);
		} while (iTo != -1);
		if (iFrom < strSource.length())
			buf.append(strSource.substring(iFrom));
		return buf.toString();
	}

	public static String[] split(String strString, String strDelimiter)
	{
		String strStrings[] = null;
		int nDelimPos = strString.indexOf(strDelimiter);
		if (nDelimPos == -1)
		{
			strStrings = (new String[] {
				strString
			});
		} else
		{
			List strings = new ArrayList(5);
			int nStartPos = 0;
			String strToken = null;
			do
			{
				if (nDelimPos == -1)
					break;
				strToken = strString.substring(nStartPos, nDelimPos);
				strings.add(strToken);
				nStartPos = nDelimPos + strDelimiter.length();
				nDelimPos = strString.indexOf(strDelimiter, nStartPos);
				if (nDelimPos == -1)
				{
					strToken = strString.substring(nStartPos);
					strings.add(strToken);
				}
			} while (true);
			strStrings = new String[strings.size()];
			strings.toArray(strStrings);
		}
		return strStrings;
	}

	public static Vector splitString(String strParameters, String strDelimiter)
	{
		Vector vecParameters = new Vector();
		int nDelimPos = strParameters.indexOf(strDelimiter);
		if (nDelimPos == -1)
		{
			vecParameters.addElement(strParameters);
		} else
		{
			int nStartPos = 0;
			String strToken = null;
			do
			{
				if (nDelimPos == -1)
					break;
				strToken = strParameters.substring(nStartPos, nDelimPos);
				vecParameters.addElement(strToken);
				nStartPos = nDelimPos + strDelimiter.length();
				nDelimPos = strParameters.indexOf(strDelimiter, nStartPos);
				if (nDelimPos == -1)
				{
					strToken = strParameters.substring(nStartPos);
					vecParameters.addElement(strToken);
				}
			} while (true);
		}
		return vecParameters;
	}

	public static String escape(String strText, char character)
	{
		if (strText != null)
		{
			int nIdx = strText.indexOf(character);
			if (nIdx > -1)
			{
				int nTextLen = strText.length();
				StringBuffer strEscaped = new StringBuffer(nTextLen + 10);
				char cPrev = '\0';
				for (int iTextIdx = 0; iTextIdx < nTextLen; iTextIdx++)
				{
					char ch = strText.charAt(iTextIdx);
					if (cPrev != '\\' && ch == character)
						strEscaped.append('\\');
					strEscaped.append(ch);
					cPrev = ch;
				}

				strText = strEscaped.toString();
			}
		}
		return strText;
	}

	public static String unicodeEscape(String str)
	{
		StringBuffer buf = new StringBuffer(str.length() + 16);
		char ch[] = str.toCharArray();
		for (int idxch = 0; idxch < ch.length; idxch++)
		{
			if (ch[idxch] == '%')
			{
				buf.append("%25");
				continue;
			}
			String strhex = Integer.toHexString(ch[idxch]);
			switch (strhex.length())
			{
			case 1: // '\001'
			case 2: // '\002'
				if (ch[idxch] >= 0 && ch[idxch] < '\200')
					buf.append(ch[idxch]);
				else
					buf.append('%').append(strhex);
				break;

			case 3: // '\003'
				buf.append("%u0").append(strhex);
				break;

			case 4: // '\004'
			default:
				buf.append("%u").append(strhex);
				break;
			}
		}

		return buf.toString();
	}

	public static String unicodeUnescape(String str)
	{
		int strlen = str.length();
		char ch[] = new char[strlen];
		int numch = 0;
		int idxstr = 0;
		do
		{
			if (idxstr >= strlen)
				break;
			char c = str.charAt(idxstr);
			boolean fCharProcessed = false;
			if (c == '%' && idxstr < strlen - 2)
				if (str.charAt(idxstr + 1) != 'u')
					try
					{
						String strval = str.substring(idxstr + 1, idxstr + 3);
						char ascii = (char)Integer.parseInt(strval, 16);
						fCharProcessed = true;
						idxstr += 3;
						ch[numch] = ascii;
						numch++;
					}
					catch (NumberFormatException e) { }
				else
				if (idxstr < strlen - 5)
					try
					{
						String strval = str.substring(idxstr + 2, idxstr + 6);
						char unicode = (char)Integer.parseInt(strval, 16);
						fCharProcessed = true;
						idxstr += 6;
						ch[numch] = unicode;
						numch++;
					}
					catch (NumberFormatException e) { }
			if (!fCharProcessed)
			{
				ch[numch] = c;
				numch++;
				idxstr++;
			}
		} while (true);
		return new String(ch, 0, numch);
	}

	public static boolean isIntegerString(String strValue, String errMsg)
	{
		try
		{
			Integer.parseInt(strValue);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}

	public static boolean isLongString(String strValue, String errMsg)
	{
		try
		{
			Long.parseLong(strValue);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}

	public static String toUnsignedString(int i, int shift)
	{
		char buf[] = new char[32];
		int charPos = 32;
		int radix = 1 << shift;
		int mask = radix - 1;
		do
		{
			buf[--charPos] = s_digits[i & mask];
			i >>>= shift;
		} while (i != 0);
		return new String(buf, charPos, 32 - charPos);
	}

	public static String toUnsignedString(long n, int shift)
	{
		char buf[] = new char[64];
		int charPos = 64;
		int radix = 1 << shift;
		long mask = radix - 1;
		do
		{
			buf[--charPos] = s_digits[(int)(n & mask)];
			n >>>= shift;
		} while (n != 0L);
		return new String(buf, charPos, 64 - charPos);
	}

	public static String concatenate(String input[], char delimiter)
	{
		if (input == null || input.length == 0)
			return null;
		String csString = null;
		if (input.length == 1)
		{
			csString = input[0];
		} else
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < input.length; i++)
			{
				buf.append(delimiter);
				buf.append(input[i]);
			}

			csString = buf.substring(1);
		}
		return csString;
	}

	public static String join(List values, String delimiter)
	{
		StringBuffer sb = new StringBuffer();
		if (values != null && values.size() > 0)
		{
			for (int i = 0; i < values.size() - 1; i++)
			{
				sb.append(values.get(i));
				sb.append(delimiter);
			}

			sb.append(values.get(values.size() - 1));
		}
		return sb.toString();
	}

	public static String encodeComma(String name)
	{
		String encodedName = name;
		if (encodedName.indexOf(',') != -1)
			encodedName = encodedName.replace(",", "#~@");
		return encodedName;
	}

	public static String decodeComma(String name)
	{
		String decodeName = name;
		if (decodeName.indexOf("#~@") != -1)
			decodeName.replace("#~@", ",");
		return decodeName;
	}

}
