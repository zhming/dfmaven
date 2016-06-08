package com.gihow.cache;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-6-8
 * Time: 下午2:04
 * To change this template use File | Settings | File Templates.
 */
public class MyListener implements RemovalListener {
    private Logger log = Logger.getLogger(MyListener.class);

    public void onRemoval(RemovalNotification notification) {
        log.debug(notification.getKey());
        log.debug(notification.getCause().name());
        log.debug(notification.getValue());
    }
}
