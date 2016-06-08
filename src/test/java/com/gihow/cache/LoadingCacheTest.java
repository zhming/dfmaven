package com.gihow.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-6-7
 * Time: 下午3:37
 * To change this template use File | Settings | File Templates.
 */
public class LoadingCacheTest {
    private boolean isCreate = false;


    @Test
    public void TestLoadingCache() throws Exception{
        LoadingCache<String,String> cahceBuilder= CacheBuilder
                .newBuilder()
                .build(new CacheLoader<String, String>(){
                    @Override
                    public String load(String key) throws Exception {
                        String strProValue="hello "+key+"!";
                        return strProValue;
                    }

                });

        System.out.println("jerry value:"+cahceBuilder.apply("jerry"));
        System.out.println("jerry value:"+cahceBuilder.get("jerry"));
        System.out.println("peida value:"+cahceBuilder.get("peida"));
        System.out.println("peida value:"+cahceBuilder.apply("peida"));
        System.out.println("lisa value:"+cahceBuilder.apply("lisa"));
        cahceBuilder.put("harry", "ssdded");
        System.out.println("harry value:"+cahceBuilder.get("harry"));
    }

    @Test
    public void testcallableCache()throws Exception{
        Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(1000).build();
        String resultVal = cache.get("jerry", new Callable<String>() {
            public String call() {
                String strProValue="hello "+"jerry"+"!";
                return strProValue;
            }
        });
        System.out.println("jerry value : " + resultVal);

        resultVal = cache.get("peida", new Callable<String>() {
            public String call() {
                String strProValue="hello "+"peida"+"!";
                return strProValue;
            }
        });
        System.out.println("peida value : " + resultVal);
    }

    @Test
    public void loadingCache1Test() throws ExecutionException {
        LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
                .maximumSize(100000)
                .expireAfterWrite(10, TimeUnit.HOURS)
                .removalListener(new MyListener())
                .build(
                        new CacheLoader<String, String>() {
                            @Override
                            public String load(String key) throws Exception {
                                return createExpensiveGraph(key);
                            }
                        }
                );

        try {
            System.out.println(loadingCache.get("world"));
            loadingCache.invalidate("world");
            System.out.println(loadingCache.get("world"));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        }
    }


    private String createExpensiveGraph(String key){
        if(key.equals("world") && isCreate == false){
            isCreate = true;
            return "Hello, " + key;
        }
        return null;
    }
}
