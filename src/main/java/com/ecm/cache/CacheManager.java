package com.ecm.cache;

import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfUser;
import com.google.common.cache.LoadingCache;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-6-8
 * Time: 下午3:02
 * To change this template use File | Settings | File Templates.
 */
public class CacheManager {
    private static Logger log = Logger.getLogger(CacheManager.class);

    private static CacheManager cacheManager = new CacheManager();

    private LoadingCache<String, IDfUser> usersCache = null;
    private LoadingCache<String, IDfFolder> cabinetCache = null;
    private LoadingCache<String, String> cabinetStringCache = null;

    private CacheManager(){
        super();
        this.init();
    }

    public static CacheManager getCacheManager(){
        return cacheManager;
    }

    /**
     * 初始化所有缓存
     */
    private void init(){
        log.debug("init invoker!");
        this.initUsers();
    }

    /**
     * 初始化UsersCache
     */
    private void initUsers(){
        log.debug("initUsers invoker!");
    }


}
