// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IValidator.java

package com.gihow.dfc;


public interface IValidator
{

	public abstract boolean getIsValid();

	public abstract void validate();

	public abstract String getErrorMessage();
}
