// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HostUtil.java

package com.gihow.util;

import java.io.Serializable;

public class HostUtil
	implements Serializable
{

	public HostUtil()
	{
	}

	private static String getHost(String hostAndPort)
	{
		String hostName = "";
		String TOKENS[] = {
			"]:", "]", ":"
		};
		int i = 0;
		do
		{
			if (i >= TOKENS.length)
				break;
			int index = hostAndPort.indexOf(TOKENS[i]);
			if (index > -1)
			{
				switch (i)
				{
				case 0: // '\0'
				case 1: // '\001'
					hostName = hostAndPort.substring(0, index + 1);
					break;

				case 2: // '\002'
					if (getCount(hostAndPort, ":") > 1)
						hostName = (new StringBuilder()).append("[").append(hostAndPort).append("]").toString();
					else
						hostName = hostAndPort.substring(0, index);
					break;
				}
				break;
			}
			i++;
		} while (true);
		if (i == TOKENS.length && hostAndPort != null && hostAndPort.length() > 0)
			hostName = hostAndPort;
		return hostName;
	}

	private static String getPort(String hostAndPort)
	{
		String portNumber = "";
		String TOKENS[] = {
			"]:", ":"
		};
		int i = 0;
		do
		{
			if (i >= TOKENS.length)
				break;
			int index = hostAndPort.indexOf(TOKENS[i]);
			if (index > -1)
			{
				switch (i)
				{
				case 0: // '\0'
					portNumber = hostAndPort.substring(index + TOKENS[i].length(), hostAndPort.length());
					break;

				case 1: // '\001'
					if (getCount(hostAndPort, ":") == 1)
						portNumber = hostAndPort.substring(index + 1, hostAndPort.length());
					break;
				}
				break;
			}
			i++;
		} while (true);
		return portNumber;
	}

	public static String getHostName(String hostAndPort)
	{
		return getHost(hostAndPort);
	}

	public static String getPortNumber(String hostAndPort)
	{
		return getPort(hostAndPort);
	}

	private static int getCount(String strHostAndPort, String substr)
	{
		String temp = strHostAndPort;
		int count = 0;
		for (int i = temp.indexOf(substr); i >= 0; i = temp.indexOf(substr))
		{
			count++;
			temp = temp.substring(i + 1);
		}

		return count;
	}
}
