package com.skypro.shelterbot.service;

import com.skypro.shelterbot.component.TelegramBot;
import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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

    public Optional<Report> getById(Long id) {
        return reportRepository.findById(id);
    }

    public List<Report> getByChatId(Long chatId) {
        return reportRepository.findByUserChatId(chatId);
    }

    public List<Report> getAll() {
        return reportRepository.findAll();
    }

    @Transactional
    public void remove(@NotNull Report report) {
        reportRepository.delete(report);
    }

    @Transactional
    public void remove(@NotNull Long id) {
        reportRepository.deleteById(id);
    }

}
