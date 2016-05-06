package com.gihow.security.session;

import com.documentum.fc.client.IDfUser;

public interface SessionCredentials {
    IDfUser getCurrentUser();

}
