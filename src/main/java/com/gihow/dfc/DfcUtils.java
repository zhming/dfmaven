// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DfcUtils.java

package com.gihow.dfc;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfList;

import java.util.Collection;
import java.util.Iterator;

public class DfcUtils
{

	private static final IDfClientX s_clientX = new DfClientX();

	public DfcUtils()
	{
	}

	public static IDfClientX getClientX()
	{
		return s_clientX;
	}

	public static IDfList dfList(Collection coll)
	{
		return dfList(coll, 1024);
	}

	public static IDfList dfList(Collection coll, int type)
	{
		IDfList list;
		list = getClientX().getList();
		list.setElementType(type);
        try {
            for (Iterator iter = coll.iterator(); iter.hasNext(); list.append(iter.next()));
        } catch (DfException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return list;
	}

}
