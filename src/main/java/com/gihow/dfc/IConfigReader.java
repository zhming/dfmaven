// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IConfigReader.java

package com.gihow.dfc;

import java.util.Iterator;

// Referenced classes of package com.documentum.web.formext.config:
//			ConfigFile

public interface IConfigReader
{

	public abstract String getAppName();

	public abstract String getRootFolderPath();

	public abstract ConfigFile loadAppConfigFile(String s);

	public abstract Iterator loadConfigFiles(String s);
}
