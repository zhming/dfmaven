package com.gihow.dfc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-13
 * Time: 下午2:47
 * To change this template use File | Settings | File Templates.
 */
public class NotificationManager implements ServletContextListener, HttpSessionListener, IConfigRefreshListener{
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        System.out.println("sessionCreated...................");
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        System.out.println("sessionDestroyed...................");
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onPostRefresh() {
        System.out.println("onPostRefresh...................");
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("contextInitialized...................");
        //调用Listener接口实现类
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("contextDestroyed...................");
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
