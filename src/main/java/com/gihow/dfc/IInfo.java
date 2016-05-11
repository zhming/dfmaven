// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IInfo.java

package com.gihow.dfc;


public interface IInfo
{

	public abstract String getEnvironmentId();

	public abstract String getEnvironmentName();

	public abstract String getEnvironmentDescription();

	public abstract String getEnvironmentMajorVersion();

	public abstract String getEnvironmentMinorVersion();

	public abstract String getEnvironmentAuthor();

	public abstract String getPortalDescription();

	public abstract String getPortalVersion();

	public abstract String getEnvironmentBaseVersion();

	public abstract String getEnvironmentServiceVersion();
}
