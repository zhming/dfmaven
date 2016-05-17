package com.gihow.mydfc.service;

import com.documentum.fc.client.IDfUser;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-16
 * Time: 下午3:52
 * To change this template use File | Settings | File Templates.
 */
public interface IDfUserService {
    public IDfUser getUserByUsername(String username) throws Exception;
    public IDfUser getUserById(String id) throws Exception;
    public List<IDfUser> getAllUsers();
}
