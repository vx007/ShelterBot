package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Dog extends Pet {

    private Long idDog;

    public Dog(String name, int age, String breed) {
        super(name, age, breed);
    }
}
