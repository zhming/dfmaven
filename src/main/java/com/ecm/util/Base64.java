// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Base64.java

package com.ecm.util;

import java.io.UnsupportedEncodingException;

public class Base64
{

	private static final char alphabet[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
	private static final byte codes[];

	public Base64()
	{
	}

	public static String decode(String data)
	{
		if (data == null)
			return null;
		else
			return utf8String(decodeBytes(data));
	}

	public static String encode(String data)
	{
		if (data == null)
			return null;
		else
			return encodeBytes(utf8Bytes(data));
	}

	public static String encodeBytes(byte data[])
	{
		if (data == null)
			return "";
		int dataLen = data.length;
		if (dataLen == 0)
			return "";
		char out[] = new char[((dataLen + 2) / 3) * 4];
		int i = 0;
		for (int index = 0; i < dataLen; index += 4)
		{
			boolean quad = false;
			boolean trip = false;
			int val = 0xff & data[i];
			val <<= 8;
			if (i + 1 < dataLen)
			{
				val |= 0xff & data[i + 1];
				trip = true;
			}
			val <<= 8;
			if (i + 2 < dataLen)
			{
				val |= 0xff & data[i + 2];
				quad = true;
			}
			out[index + 3] = alphabet[quad ? val & 0x3f : 64];
			val >>= 6;
			out[index + 2] = alphabet[trip ? val & 0x3f : 64];
			val >>= 6;
			out[index + 1] = alphabet[val & 0x3f];
			val >>= 6;
			out[index + 0] = alphabet[val & 0x3f];
			i += 3;
		}

		return new String(out);
	}

	private static String utf8String(byte bytes[])
	{
		String retVal = null;
		try
		{
			retVal = new String(bytes, "UTF-8");
		}
		catch (UnsupportedEncodingException e1)
		{
			try
			{
				retVal = new String(bytes, "UTF8");
			}
			catch (UnsupportedEncodingException e2)
			{
				throw new RuntimeException((new StringBuilder()).append("UnsupportedEncodingException: ").append(e2.getMessage()).toString());
			}
		}
		return retVal;
	}

	private static byte[] utf8Bytes(String inStr)
	{
		byte retVal[] = null;
		if (inStr == null)
			return null;
		try
		{
			retVal = inStr.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e1)
		{
			try
			{
				retVal = inStr.getBytes("UTF8");
			}
			catch (UnsupportedEncodingException e2)
			{
				throw new RuntimeException((new StringBuilder()).append("UnsupportedEncodingException: ").append(e2.getMessage()).toString());
			}
		}
		return retVal;
	}

	public static byte[] decodeBytes(String encoded)
	{
		if (encoded == null)
			return new byte[0];
		char data[] = encoded.toCharArray();
		int tempLen = data.length;
		int dataLen = tempLen;
		for (int ix = 0; ix < dataLen; ix++)
		{
			int value = codes[data[ix] & 0xff];
			if (value < 0 && data[ix] != '=')
				tempLen--;
		}

		int len = ((tempLen + 3) / 4) * 3;
		if (tempLen > 0 && data[tempLen - 1] == '=')
			len--;
		if (tempLen > 1 && data[tempLen - 2] == '=')
			len--;
		byte out[] = new byte[len];
		int shift = 0;
		int accum = 0;
		int index = 0;
		for (int ix = 0; ix < dataLen; ix++)
		{
			int value = codes[data[ix] & 0xff];
			if (value < 0)
				continue;
			accum <<= 6;
			shift += 6;
			accum |= value;
			if (shift >= 8)
			{
				shift -= 8;
				out[index++] = (byte)(accum >> shift & 0xff);
			}
		}

		return out;
	}

	static 
	{
		codes = new byte[256];
		for (int i = 0; i < 256; i++)
			codes[i] = -1;

		for (int i = 65; i <= 90; i++)
			codes[i] = (byte)(i - 65);

		for (int i = 97; i <= 122; i++)
			codes[i] = (byte)((26 + i) - 97);

		for (int i = 48; i <= 57; i++)
			codes[i] = (byte)((52 + i) - 48);

		codes[43] = 62;
		codes[47] = 63;
	}
}
