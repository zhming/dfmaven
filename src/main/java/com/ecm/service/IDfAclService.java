package com.ecm.service;

import com.documentum.fc.client.IDfACL;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-19
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
public interface IDfAclService {
    public IDfACL getAclByName(String domain, String aclName);
}
