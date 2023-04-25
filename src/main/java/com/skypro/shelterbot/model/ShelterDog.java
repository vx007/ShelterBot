package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ShelterDog {
    private HashMap<Long, Dog> dogs = new HashMap<>();
    private List<String> instructionsForDogs = new LinkedList<>();

    public HashMap<Long, Dog> getCats() {
        return dogs;
    }

    public List<String> getInstructionsForDogs() {
        return instructionsForDogs;
    }
}
