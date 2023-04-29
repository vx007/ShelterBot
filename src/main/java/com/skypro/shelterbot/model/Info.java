package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity(name = "info")
public class Info {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String name;
    private String details;

}