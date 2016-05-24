package com.gihow.sitemanager;

import com.gihow.DefaultAction;
import org.apache.log4j.Logger;
import org.owasp.esapi.User;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-23
 * Time: 上午11:24
 * To change this template use File | Settings | File Templates.
 */
public class SiteTree extends DefaultAction {
    private String roleId = "";
    private String siteId = "";
    private String tree_script = "";
    private Logger logger = Logger.getLogger(SiteTree.class);
    public String execute() {
        logger.debug("SiteTree: ..................");
        // 初始化tree_script， 文件柜、收藏夹、文件列表菜单
        tree_script = "Hello, tree_script!";
        return SUCCESS;
    }

    public String getTree_script() {
        return tree_script;
    }

    public void setTree_script(String tree_script) {
        this.tree_script = tree_script;
    }
}

