package com.skypro.shelterbot.service;

import com.skypro.shelterbot.exception.EntryNotFoundException;
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
    public Report add(@NonNull Report report) {
        if (!Objects.isNull(report.getId())) {
            report.setId(null);
        }
        return reportRepository.save(report);
    }

    public Report getById(@NonNull Long id) throws EntryNotFoundException {
        return reportRepository.findById(id).orElseThrow(() -> new EntryNotFoundException("Report not found by ID"));
    }

    public List<Report> getByChatId(@NonNull Long chatId) throws EntryNotFoundException {
        if (!reportRepository.findByUserChatId(chatId).isEmpty()){
            return reportRepository.findByUserChatId(chatId);
        } else {
            throw new EntryNotFoundException("Reports not found by chatID");
        }
    }

    public Report getLastReportByChatId(@NonNull Long chatId) throws EntryNotFoundException {
        return getByChatId(chatId).stream().max(REPORT_TIMESTAMP_COMPARATOR).orElseThrow(()->new EntryNotFoundException("Last report not found by chatID"));
    }

    public List<Report> getAll() throws EntryNotFoundException {
        if (!reportRepository.findAll().isEmpty()){
            return reportRepository.findAll();
        } else {
            throw new EntryNotFoundException("Reports not found");
        }
    }

    @Transactional
    public Report updatePhotoOnLastReport(@NonNull Long chatId, String photoId) throws EntryNotFoundException {
        var lastReport = getLastReportByChatId(chatId);
        lastReport.setPhotoId(photoId);
        return reportRepository.save(lastReport);
    }

    @Transactional
    public Report updateTextOnLastReport(@NonNull Long chatId, String text) throws EntryNotFoundException {
        var lastReport = getLastReportByChatId(chatId);
        lastReport.setText(text);
        return reportRepository.save(lastReport);
    }

    @Transactional
    public Report approveLastReport(@NonNull Long chatId) throws EntryNotFoundException {
        var lastReport = getLastReportByChatId(chatId);
        lastReport.setIsApproved(true);
        return reportRepository.save(lastReport);
    }

    @Transactional
    public void remove(@NonNull Long id) throws EntryNotFoundException {
        if (reportRepository.existsById(id)) {
            reportRepository.deleteById(id);
        } else {
            throw new EntryNotFoundException("Report not found by ID");
        }
    }
}
