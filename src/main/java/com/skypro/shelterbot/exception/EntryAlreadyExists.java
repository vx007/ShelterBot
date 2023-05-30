package com.skypro.shelterbot.exception;

public class EntryAlreadyExists extends Exception {
    public EntryAlreadyExists() {
    }

    public EntryAlreadyExists(String message) {
        super(message);
    }

    public EntryAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }

    public EntryAlreadyExists(Throwable cause) {
        super(cause);
    }
}
