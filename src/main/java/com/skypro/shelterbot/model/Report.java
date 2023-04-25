package com.skypro.shelterbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Report {

    private String text;
    private File photo;

}
