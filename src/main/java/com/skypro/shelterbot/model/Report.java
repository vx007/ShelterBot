package com.skypro.shelterbot.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

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
    private Timestamp timestamp;
    private Boolean isApproved;

    public Report(User user, String photoId, String text, Timestamp timestamp, Boolean isApproved) {
        this.user = user;
        this.photoId = photoId;
        this.text = text;
        this.timestamp = timestamp;
        this.isApproved = isApproved;
    }
}
