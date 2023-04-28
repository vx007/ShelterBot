package com.skypro.shelterbot.exeption;

public class SendTextException extends Exception {

    private final int errorCode;

    public SendTextException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
