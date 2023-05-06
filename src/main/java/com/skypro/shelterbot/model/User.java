package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    private Long chatId;
    private String name;
    private String phone;

    @Column(name = "last_cmd")
    private String lastCommand;
    private Timestamp registeredAt;

    @OneToOne
    private Pet pet;

    @OneToMany(mappedBy = "user")
    private List<Report> reports;
}
