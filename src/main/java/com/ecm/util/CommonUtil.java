package com.ecm.util;

import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;

public class CommonUtil
{
    /** */
    public static String s_BUNDLE_BASENAME = "default";
    
    /** */
    public static String 
        getText(String s)
    {
        return ResourceBundle.getBundle(s_BUNDLE_BASENAME).getString(s);
    }
    
    /** */
    public static String 
        generateUnique()
    {
        String s = Long.toHexString((new Date()).getTime()) + 
                   Long.toHexString((new Random()).nextLong());
        return s.toUpperCase();
    }
    
    private CommonUtil() { }
}