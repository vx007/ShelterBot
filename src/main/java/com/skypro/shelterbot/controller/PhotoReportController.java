package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.repositories.PhotoReportRepository;
import com.skypro.shelterbot.service.PhotoReportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("photo_report")
public class PhotoReportController {

    private final PhotoReportService photoReportService;
    private final PhotoReportRepository photoReportRepository;

    public PhotoReportController(PhotoReportService photoReportService, PhotoReportRepository photoReportRepository) {
        this.photoReportService = photoReportService;
        this.photoReportRepository = photoReportRepository;
    }

    @PostMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile photoPet) throws IOException {
        if (photoPet.getSize() > 1024 * 300){
            return ResponseEntity.badRequest().body("File is too big.");
        }
        photoReportService.uploadPhotoPet(id, photoPet);
        return ResponseEntity.ok().build();
    }
}
