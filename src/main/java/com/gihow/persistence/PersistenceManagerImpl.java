package com.gihow.persistence;

import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-5
 * Time: 上午11:09
 * To change this template use File | Settings | File Templates.
 */
public class PersistenceManagerImpl implements PersistenceManager {
    @Override
    public void save(Object objectToSave) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void remove(Object objectToRemove) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getById(Class type, long id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getByUniqueField(Class type, Object pk, String fieldName) {
        IDfUser user = null;

        return user;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List findAll(Class type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List findAllSorted(Class type, String sortProperty) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List findAllSortedDirected(Class type, String sortField, String direction) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List getList(String query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getById(Class aClass, String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
