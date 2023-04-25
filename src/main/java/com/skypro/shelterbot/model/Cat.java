package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Cat extends Pet {

    private Long idCat;

    public Cat(String name, int age, String breed) {
        super(name, age, breed);
    }
}
