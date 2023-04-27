package com.skypro.shelterbot.service;

import com.skypro.shelterbot.exceptions.ReportDataNotFoundException;
import com.skypro.shelterbot.model.ReportData;
import com.skypro.shelterbot.repository.ReportDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.pengrad.telegrambot.model.File;

import javax.transaction.Transactional;
import java.io.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReportDataService {
    private final ReportDataRepository reportRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReportDataService.class);

    public ReportDataService(ReportDataRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    public void uploadReportData(Long personId, byte[] pictureFile, File file, String ration, String health,
                                 String habits, String filePath, Date dateSendMessage, Long timeDate, long daysOfReports) throws IOException {
        logger.info("Был вызван метод для загрузки данных отчета");

        ReportData report = new ReportData();

        report.setLastMessage(dateSendMessage);
        report.setDays(daysOfReports);
        report.setFilePath(filePath);
        report.setFileSize(file.fileSize());
        report.setLastMessageMs(timeDate);
        report.setChatId(personId);
        report.setData(pictureFile);
        report.setRation(ration);
        report.setHealth(health);
        report.setHabits(habits);
        this.reportRepository.save(report);
    }


    public void uploadReportData(Long personId, byte[] pictureFile, File file,
                                 String caption, String filePath, Date dateSendMessage, Long timeDate, long daysOfReports) throws IOException {
        logger.info("Был вызван метод для загрузки данных отчета");

        ReportData report = new ReportData();//findById(personId);
        report.setLastMessage(dateSendMessage);
        report.setDays(daysOfReports);
        report.setFilePath(filePath);
        report.setChatId(personId);
        report.setFileSize(file.fileSize());
        report.setData(pictureFile);
        report.setCaption(caption);
        report.setLastMessageMs(timeDate);
        this.reportRepository.save(report);
    }


    public ReportData findById(Long id) {
        logger.info("Был вызван метод для получения отчета с помощью id={}", id);

        return this.reportRepository.findById(id)
                .orElseThrow(ReportDataNotFoundException::new);
    }


    public ReportData findByChatId(Long chatId) {
        logger.info("Был вызван метод для получения отчета с помощью chatId={}", chatId);

        return this.reportRepository.findByChatId(chatId);
    }


    public Collection<ReportData> findListByChatId(Long chatId) {
        logger.info("Был вызван метод для поиска списка отчета по id={}", chatId);

        return this.reportRepository.findListByChatId(chatId);
    }


    public ReportData save(ReportData report) {
        logger.info("Был вызван метод для сохранения отчета");

        return this.reportRepository.save(report);
    }


    public void remove(Long id) {
        logger.info("Был вызван метод для удаления отчета с помощью id={}", id);

        this.reportRepository.deleteById(id);
    }


    public List<ReportData> getAll() {
        logger.info("Был вызван метод для получения всех отчетов");

        return this.reportRepository.findAll();
    }


    public List<ReportData> getAllReports(Integer pageNumber, Integer pageSize) {
        logger.info("Был вызван метод для получения всех отчетов");

        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return this.reportRepository.findAll(pageRequest).getContent();
    }


    private String getExtensions(String fileName) {
        logger.info("Был вызван метод для получения расширений");

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
