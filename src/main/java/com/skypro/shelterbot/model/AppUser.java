package com.skypro.shelterbot.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity(name = "usersDataTable")
public class AppUser {

    @Id
    private Long chatId;
//    private Boolean isThereAnAnimal;
    private String firstName;
    private String lastName;
    private String userName;
    private Timestamp registeredAt;
    private String petName;

}
