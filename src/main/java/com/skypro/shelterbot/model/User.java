package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    private Long chatId;
    private String shelter;
    private String name;
    private String phone;
    private String mail;
    private Timestamp registeredAt;
}
