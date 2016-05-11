// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ISession.java

package com.gihow.dfc;

import java.security.Principal;

// Referenced classes of package com.documentum.web.env:
//			EnvironmentPreferenceException

public interface ISession
{

	public abstract String getUsername();

	public abstract Principal getUserPrincipal();

	public abstract String getDocbaseName()
		throws EnvironmentPreferenceException;

	public abstract String getUserPassword();

	public abstract String getDomain();
}
