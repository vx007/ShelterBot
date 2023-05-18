package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

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

    @Enumerated(value = EnumType.STRING)
    private TrialPeriod period;
    private Boolean needVolunteer;

    @OneToOne
    private Pet pet;

    public User(Long chatId, String name, Timestamp registeredAt) {
        this(chatId, name, null, null, registeredAt, null, false, null);
    }
}
