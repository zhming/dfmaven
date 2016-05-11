// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IPreference.java

package com.gihow.dfc;

import java.util.ArrayList;

// Referenced classes of package com.documentum.web.env:
//			EnvironmentPreferenceException, EnvironmentConfigScope

public interface IPreference
{

	public abstract String lookupString(String s, EnvironmentConfigScope environmentconfigscope)
		throws EnvironmentPreferenceException;

	public abstract Boolean lookupBoolean(String s, EnvironmentConfigScope environmentconfigscope)
		throws EnvironmentPreferenceException;

	public abstract Integer lookupInteger(String s, EnvironmentConfigScope environmentconfigscope)
		throws EnvironmentPreferenceException;

	public abstract boolean writeBoolean(String s, Boolean boolean1, EnvironmentConfigScope environmentconfigscope)
		throws EnvironmentPreferenceException;

	public abstract boolean writeString(String s, String s1, EnvironmentConfigScope environmentconfigscope)
		throws EnvironmentPreferenceException;

	public abstract boolean writeInteger(String s, Integer integer, EnvironmentConfigScope environmentconfigscope)
		throws EnvironmentPreferenceException;

	public abstract ArrayList getPreferenceScopes();

	public abstract ArrayList getWriteModePreferenceScopes();

	public abstract String externalizePreference(String s);
}
