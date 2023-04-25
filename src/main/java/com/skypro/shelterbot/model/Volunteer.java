package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Volunteer {

    private Long idVolunteer;
    private String firstName;
    private String lastName;
    private String phone;

    private HashMap<Long, Pet> petsOfOneVolunteer = new HashMap<>();
}
