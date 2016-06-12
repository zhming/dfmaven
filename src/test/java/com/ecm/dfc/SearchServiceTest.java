package com.ecm.dfc;

import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.search.*;
import com.documentum.fc.client.search.impl.definition.metadata.DfSearchSource;
import com.documentum.fc.common.DfException;
import com.ecm.dfc.sessionmananger.ClientXUtils;
import com.ecm.util.StaticValuesUtil;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-6-2
 * Time: 上午9:50
 * To change this template use File | Settings | File Templates.
 */
public class SearchServiceTest {
    private Logger log = Logger.getLogger(SearchServiceTest.class);
    @Test
    public void getSourceMapTest(){
       try {
           IDfSearchService searchService = ClientXUtils.getSearchService(StaticValuesUtil.GLOBAL_USERNAME, StaticValuesUtil.GLOBAL_PASSWORD);
           IDfSearchSourceMap sourceMap = searchService.getSourceMap();
           IDfEnumeration enumeration =  sourceMap.getAvailableSources();
           while (enumeration.hasMoreElements()){
               DfSearchSource obj = (DfSearchSource)enumeration.nextElement();

               log.debug("getDeDuplicationKey | " + obj.getDeDuplicationKey());
               log.debug("getDescription | " + obj.getDescription());
               log.debug("getHomeURL | " + obj.getHomeURL());
               log.debug("getModificationKeys | " + obj.getModificationKeys());
               log.debug("getName | " + obj.getName());
               log.debug("getServerVersion | " + obj.getServerVersion());
               log.debug("getType | " + obj.getType());
           }
           IDfQueryManager queryManager =  searchService.newQueryMgr();
           IDfQueryBuilder query = queryManager.newQueryBuilder("dm_document");
           IDfExpressionSet root = query.getRootExpressionSet();

           IDfQueryProcessor processor =
                   searchService.newQueryProcessor(query, true /* noDuplicates */);
           processor.blockingSearch(0);
           IDfResultsSet results = processor.getResults();
           while (results.next()){
               IDfResultEntry entry =  results.getResult();
               IDfEnumeration e = entry.enumAttrs();
               while (e.hasMoreElements()){
                   log.debug(" | " + e.nextElement());
               }
           }

       } catch (DfException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       } catch (InterruptedException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       }
    }

}
