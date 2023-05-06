package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private PetType type;
    private String name;
    private Integer age;
    private String breed;

    public Pet(PetType type, String name, Integer age, String breed) {
        this.type = type;
        this.name = name;
        this.age = age;
        this.breed = breed;
    }
}
