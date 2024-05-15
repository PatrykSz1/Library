package com.testlibrary.testlibrary.exception.book;

import jakarta.persistence.PersistenceException;

public class EntityIsUnavailableException extends PersistenceException {

    public EntityIsUnavailableException(String message) {
        super(message);
    }
}
