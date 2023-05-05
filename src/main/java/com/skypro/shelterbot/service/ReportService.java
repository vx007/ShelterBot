package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public void add(Report report) {
        var id = report.getId();
        if (reportRepository.findById(id).isEmpty()) {
            reportRepository.save(report);
        }
    }

    public Optional<Report> getById(Long id) {
        return reportRepository.findById(id);
    }

    public Optional<Report> getByChatId(Long chatId) {
        return reportRepository.findByUserChatId(chatId);
    }

    public Optional<Report> getByPhotoId(Long photoId) {
        return reportRepository.findByPhotoId(photoId);
    }

    public List<Report> getAll() {
        return reportRepository.findAll();
    }

    @Transactional
    public void remove(Report report) {
        var id = report.getId();
        if (reportRepository.findById(id).isPresent()) {
            reportRepository.delete(report);
        }
    }

    @Transactional
    public void remove(Long id) {
        if (reportRepository.findById(id).isPresent()) {
            reportRepository.deleteById(id);
        }
    }
}
