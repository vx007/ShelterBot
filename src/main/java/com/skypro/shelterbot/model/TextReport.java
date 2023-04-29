package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class TextReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private String infoPet;
    private LocalDateTime dateTime;
    private boolean quality;

    @OneToOne
    private PhotoReport photoReport;

    @ManyToOne
    @JoinColumn(name = "shelter_dog_user_id")
    private ShelterDogUser shelterDogUser;

    @ManyToOne
    @JoinColumn(name = "shelter_cat_user_id")
    private ShelterCatUser shelterCatUser;

}
