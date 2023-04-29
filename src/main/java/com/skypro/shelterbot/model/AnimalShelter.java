package com.skypro.shelterbot.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "AnimalShelter")
@Data
public class AnimalShelter {
    @Id

    private Integer id;

    private String ShelterName;


}
