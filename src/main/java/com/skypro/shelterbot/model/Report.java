package com.skypro.shelterbot.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private User user;
    private String photoId;
    private String text;
    private LocalDateTime dateTime;
    private Boolean isApproved;

    public Report(User user, String photoId, String text, LocalDateTime dateTime, Boolean isApproved) {
        this.user = user;
        this.photoId = photoId;
        this.text = text;
        this.dateTime = dateTime;
        this.isApproved = isApproved;
    }
}
