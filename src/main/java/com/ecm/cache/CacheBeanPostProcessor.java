package com.ecm.cache;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-6-8
 * Time: 下午3:35
 * To change this template use File | Settings | File Templates.
 */
public class CacheBeanPostProcessor implements BeanPostProcessor {
    private Logger log = Logger.getLogger(CacheBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        log.debug("CacheBeanPostProcessor postProcessBeforeInitialization!");

        return o;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        log.debug("CacheBeanPostProcessor postProcessAfterInitialization!");
        CacheManager.getCacheManager();
        return o;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
