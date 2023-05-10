package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.repository.ReportRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public void add(@NotNull Report report) {
        reportRepository.save(report);
    }

    public Report getById(@NonNull Long id) {
        return reportRepository.findById(id).orElseThrow();
    }

    public List<Report> getByChatId(@NonNull Long chatId) {
        return reportRepository.findByUserChatId(chatId);
    }

    public List<Report> getAll() {
        return reportRepository.findAll();
    }

    public void approve(@NonNull Long chatId) {
        var report = reportRepository.findById(chatId).orElseThrow();
        report.setIsApproved(true);
        reportRepository.save(report);
    }

    @Transactional
    public void remove(@NotNull Long id) {
        if (reportRepository.existsById(id)) {
            reportRepository.deleteById(id);
        }
    }
}
