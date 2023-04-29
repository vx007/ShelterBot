package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.PhotoReport;
import com.skypro.shelterbot.repositories.PhotoReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class PhotoReportService {

    @Value("photo_report")
    private String coversDir;

    private final PetService petService;
    private final PhotoReportRepository photoReportRepository;

    public PhotoReportService(PetService petService, PhotoReportRepository photoReportRepository) {
        this.petService = petService;
        this.photoReportRepository = photoReportRepository;
    }

    public void uploadPhotoPet(Long petId, MultipartFile file) throws IOException {
        Pet pet = petService.findPet(petId);

        Path filePath = Path.of(coversDir, petId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }

        PhotoReport photoReport = findPhotoReport(petId);
        photoReportRepository.save(photoReport);
        photoReport.setPet(pet);
        photoReport.setFilePath(filePath.toString());
        photoReport.setFileSize(file.getSize());
        photoReport.setMediaType(file.getContentType());

        photoReportRepository.save(photoReport);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public PhotoReport findPhotoReport(Long petId) {
        return photoReportRepository.findPhotoReportById(petId).orElse(new PhotoReport());
    }

    public PhotoReport savePhotoReport (PhotoReport photoReport) {
        return photoReportRepository.save(photoReport);
    }

}