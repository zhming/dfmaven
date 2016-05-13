// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TrustedAuthenticatorTool.java

package com.gihow.dfc;

import java.io.IOException;
import java.security.GeneralSecurityException;

// Referenced classes of package com.documentum.web.formext.session:
//			TrustedAuthenticatorUtils

public final class TrustedAuthenticatorTool
{

	public TrustedAuthenticatorTool()
	{
	}

	public static void main(String args[])
	{
		if (args.length < 1)
		{
			System.out.println("usage: TrustedAuthenticatorTool <string1> <string2> ...");
			return;
		}
		for (int nIdx = 0; nIdx < args.length; nIdx++)
		{
			try
			{
				Thread.sleep(1000L);
			}
			catch (InterruptedException e) { }
			try
			{
				String strE = TrustedAuthenticatorUtils.encryptByDES(args[nIdx]);
				String strD = TrustedAuthenticatorUtils.decryptByDES(strE);
				System.out.println((new StringBuilder()).append("Encrypted: [").append(strE).append("], Decrypted: [").append(strD).append("]").toString());
			}
			catch (GeneralSecurityException gse)
			{
				gse.printStackTrace();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}

	}
}
