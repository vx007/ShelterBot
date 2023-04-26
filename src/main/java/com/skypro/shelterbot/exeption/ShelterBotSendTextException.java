package com.skypro.shelterbot.exeption;

public class ShelterBotSendTextException extends Exception {

    private final int errorCode;

    public ShelterBotSendTextException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
