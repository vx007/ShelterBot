package com.skypro.shelterbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skypro.shelterbot.enums.TrialPeriod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity(name = "shelter_cat_user")
public class ShelterCatUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String name;
    private String mail;
    private String phone;
    private LocalDate startTrialPeriod;
    private LocalDate endTrialPeriod;
    @Enumerated(value = EnumType.STRING)
    private TrialPeriod trialPeriod;

    @OneToOne
    private Pet pet;

    @OneToMany(mappedBy = "shelterCatUser")
    private List<ReportPet> reportPets;
}