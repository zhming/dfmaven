// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IConfigElement.java

package com.gihow.dfc;

import java.io.Serializable;
import java.util.Iterator;

public interface IConfigElement
	extends Serializable
{

	public abstract String getName();

	public abstract IConfigElement getParent();

	public abstract void setParent(IConfigElement iconfigelement);

	public abstract String getValue();

	public abstract Boolean getValueAsBoolean();

	public abstract Integer getValueAsInteger();

	public abstract String getAttributeValue(String s);

	public abstract Boolean getAttributeValueAsBoolean(String s);

	public abstract Integer getAttributeValueAsInteger(String s);

	public abstract void setAttributeValue(String s, String s1);

	public abstract void setAttributeValue(String s, boolean flag);

	public abstract void setAttributeValue(String s, int i);

	public abstract Iterator getAttributeNames();

	public abstract Iterator getChildElements();

	public abstract Iterator getChildElements(String s);

	public abstract IConfigElement getChildElement(String s);

	public abstract String getChildValue(String s);

	public abstract Boolean getChildValueAsBoolean(String s);

	public abstract Integer getChildValueAsInteger(String s);

	public abstract IConfigElement getDescendantElement(String s);

	public abstract String getDescendantValue(String s);

	public abstract Boolean getDescendantValueAsBoolean(String s);

	public abstract Integer getDescendantValueAsInteger(String s);

	public abstract void addChildElement(IConfigElement iconfigelement);
}
