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
public class ShelterCat {

    private HashMap<Long, Cat> cats = new HashMap<>();
    private List<String> instructionsForCats = new LinkedList<>();

    public HashMap<Long, Cat> getCats() {
        return cats;
    }

    public List<String> getInstructionsForCats() {
        return instructionsForCats;
    }
}
