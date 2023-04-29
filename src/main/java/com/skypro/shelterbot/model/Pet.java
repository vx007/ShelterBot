package com.skypro.shelterbot.model;

import com.skypro.shelterbot.enums.WhichPet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;

    @Enumerated(value = EnumType.STRING)
    private WhichPet whichPet;
    private String breed;

    @OneToMany(mappedBy = "pet")
    private List<PhotoReport> photoReports;

    public Pet(String name, int age, WhichPet whichPet, String breed) {
    }
}