package com.gihow.persistence;

public class PersistenceException extends RuntimeException {

    public PersistenceException(Throwable cause) {
        super(cause);
    }

    public PersistenceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
