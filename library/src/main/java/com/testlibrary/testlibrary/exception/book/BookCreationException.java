package com.testlibrary.testlibrary.exception.book;

public class BookCreationException extends IllegalArgumentException {
    public BookCreationException(String message) {
        super(message);
    }
}
