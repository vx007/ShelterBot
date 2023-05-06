package com.skypro.shelterbot.model;

public enum PetType {
    CAT("Кошка"),
    DOG("Собака");
    private final String description;

    PetType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
