package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.repository.ReportRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import java.util.Objects;


@RequiredArgsConstructor
@Service
public class ReportService {
    private static final Comparator<Report> REPORT_TIMESTAMP_COMPARATOR = Comparator.comparing(Report::getTimestamp);
    private final ReportRepository reportRepository;

    @Transactional

    public Report add(@NotNull Report report) {
        return reportRepository.save(report);
    }

   
    public Report getById(@NonNull Long id) {

        return reportRepository.findById(id).orElseThrow();
    }

    public List<Report> getByChatId(@NonNull Long chatId) {
        return reportRepository.findByUserChatId(chatId);
    }

    public Report getLastReportByChatId(@NonNull Long chatId) {
        return getByChatId(chatId).stream().max(REPORT_TIMESTAMP_COMPARATOR).orElseThrow();
    }

    public List<Report> getAll() {
        return reportRepository.findAll();
    }

    @Transactional
    public Report updatePhotoOnLastReport(@NonNull Long chatId, String photoId) {
        var lastReport = getById(chatId);
        lastReport.setPhotoId(photoId);
        return reportRepository.save(lastReport);
    }

    @Transactional
    public Report updateTextOnLastReport(@NonNull Long chatId, String text) {
        var lastReport = getById(chatId);
        lastReport.setText(text);
        return reportRepository.save(lastReport);
    }

    @Transactional
    public Report approveLastReport(@NonNull Long chatId) {
        var lastReport = getLastReportByChatId(chatId);
        lastReport.setIsApproved(true);
        return reportRepository.save(lastReport);
    }

    @Transactional
    public void remove(@NonNull Long id) {
        if (reportRepository.existsById(id)) {
            reportRepository.deleteById(id);
        }
    }
}
