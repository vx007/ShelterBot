package com.skypro.shelterbot.exception;

public class EntryNotFoundException extends Exception {
    public EntryNotFoundException() {
    }

    public EntryNotFoundException(String message) {
        super(message);
    }

    public EntryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntryNotFoundException(Throwable cause) {
        super(cause);
    }
}
