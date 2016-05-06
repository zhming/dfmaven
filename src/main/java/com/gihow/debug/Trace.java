package com.gihow.debug;

import com.documentum.fc.common.DfLogger;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-3
 * Time: 下午2:54
 * To change this template use File | Settings | File Templates.
 */
public class Trace {
    /**
     * 打印对象的调试信息
     * @param obj
     * @param msg
     */
    public static void println(Object obj, String msg){
        String strPlainMessage = msg.replace('{', '[').replace('}', ']');
        //DfLogger.debug(obj, strPlainMessage, null, null);
        Logger.getLogger(obj.getClass()).debug(msg);
    }


}
