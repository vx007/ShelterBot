package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.glassfish.grizzly.http.util.TimeStamp;

import javax.persistence.Entity;
import javax.persistence.Id;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity(name = "users")
public class User{

    @Id
    private Long chatId;
    private String shelter;
    private String name;
    private String phone;
    private String mail;
    private TimeStamp registeredAt;

}