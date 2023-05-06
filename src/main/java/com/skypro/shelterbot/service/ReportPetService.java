package com.skypro.shelterbot.service;

import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.skypro.shelterbot.model.PhotoReport;
import com.skypro.shelterbot.model.ReportPet;
import com.skypro.shelterbot.model.ShelterCatUser;
import com.skypro.shelterbot.model.ShelterDogUser;
import com.skypro.shelterbot.repositories.ReportPetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class ReportPetService {
    private final ShelterUserService shelterUserService;
    private final PhotoReportService photoReportService;
    private final ReportPetRepository reportPetRepository;

    @Autowired
    private TelegramBot telegramBot;
    private final String coversDir = "C://Users//32227//IdeaProjects";


    public ReportPetService(ShelterUserService shelterUserService, PhotoReportService photoReportService, ReportPetRepository reportPetRepository) {
        this.shelterUserService = shelterUserService;
        this.photoReportService = photoReportService;
        this.reportPetRepository = reportPetRepository;
    }

    /**
     * Метод отправляет ежедневный отчет усыновителя, включающиий  фото питомца, рацион, самочувствие, поведение. Отчет сохраняется в БД в таблице ReportPet
     */
    public ReportPet sendReport(Long chatId, String caption, PhotoSize[] photoSizes) throws IOException {
        ReportPet reportPet = null;
        try {
            reportPet = getNewReport(chatId, photoSizes, caption);
        } catch (IllegalArgumentException e) { // владелец не найден по айди или чат айди

        }

        return reportPetRepository.save(reportPet);
    }

    /**
     * Метод создает объекты типа PhotoPet и KeepingPet
     * Отправляет эти объекты в базу данных
     * Сохраняет фотографию питомца на сервер в папку
     */
    private ReportPet getNewReport(Long chatId, PhotoSize[] photoSizes, String caption) throws IOException {
        PhotoSize photo = photoSizes[1];
        String fileId = photo.fileId();

        GetFile fileRequest = new GetFile(fileId);
        GetFileResponse fileResponse = telegramBot.execute(fileRequest);
        File file = fileResponse.file();
        byte[] fileData = telegramBot.getFileContent(file);

        Path filePath = Path.of(coversDir, fileId + "." +getExtension(file.filePath()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        long reportId = findReportIdByChatId(chatId);

        PhotoReport photoReport;
        if (reportId == -1) { // пользователь сегодня еще не отправлял отчет
            photoReport = createPhotoPet(chatId, fileRequest, file, filePath);
            photoReportService.savePhotoReport(photoReport);
            uploadPhotoToServer(fileData, filePath);

            return createReportPet(chatId, caption, photoReport);
        }
        else { // пользователь сегодня уже отправлял отчет
            PhotoReport deletePhotoPet = reportPetRepository.findReportPetById(reportId).getPhotoReport();
            Files.deleteIfExists(Path.of(deletePhotoPet.getFilePath()));

            photoReport = updatePhotoReport(reportId, fileRequest, file, filePath);
            photoReportService.savePhotoReport(photoReport);
            uploadPhotoToServer(fileData, filePath);

            return updateReportPet(reportId, caption, photoReport);
        }
    }

    /**
     * Метод для поиска айди отчета, отправленного владельцем питомца сегодня
     * @return айди искомого отчета
     */
    private long findReportIdByChatId(Long chatId) {

        ShelterCatUser shelterCatUser = shelterUserService.findCatUser(chatId);
        ShelterDogUser shelterDogUser = shelterUserService.findDogUser(chatId);

        List<ReportPet> reportsToday = (List<ReportPet>) getAllReportPet(LocalDate.now());
        long reportId = -1; // идентификатор  отчета, отправленного пользователем сегодня
        if (shelterCatUser != null) {
            for (ReportPet reportPet : reportsToday) {
                if (reportPet.getShelterCatUser().equals(shelterCatUser)) {
                    reportId = reportPet.getId();
                    break;
                }
            }
        } else if (shelterDogUser != null) {
            for (ReportPet reportPet : reportsToday) {
                if (reportPet.getShelterDogUser().equals(shelterDogUser)) {
                    reportId = reportPet.getId();
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException("Владельца с таким chatId не существует:" + chatId);
        }
        return reportId;
    }

    /**
     * Метод создает объект типа PhotoPet
     *
     * @param chatId Идентификатор чата
     * @param fileRequest объект класса GetFile
     * @param file объект класса File
     * @param filePath путь к файлу
     * @return созданный объект класса PhotoPet
     */
    private PhotoReport createPhotoPet(Long chatId, GetFile fileRequest, File file, Path filePath) {

        PhotoReport photoReport = new PhotoReport();
        photoReport.setMediaType(fileRequest.getContentType());
        photoReport.setFileSize(file.fileSize());
        photoReport.setFilePath(filePath.toString());

        ShelterCatUser shelterCatUser = shelterUserService.findCatUser(chatId);
        ShelterDogUser shelterDogUser = shelterUserService.findDogUser(chatId);
        if (shelterCatUser != null) {
            photoReport.setPet(shelterCatUser.getPet());
        }else if (shelterDogUser != null) {
            photoReport.setPet(shelterDogUser.getPet());
        }else {
            throw new IllegalArgumentException("Владельца с таким chatId не существует:" + chatId);
        }

        return photoReport;
    }
    /**
     * Метод обновляет объект типа PhotoPet
     *
     * @param reportId айди отчета о питомце
     * @param fileRequest объект класса GetFile
     * @param file объект класса File
     * @param filePath путь к файлу
     * @return созданный объект класса PhotoPet
     */
    private PhotoReport updatePhotoReport(Long reportId, GetFile fileRequest, File file, Path filePath) {

        ReportPet reportPet = reportPetRepository.findReportPetById(reportId);
        PhotoReport photoReport = reportPet.getPhotoReport();

        photoReport.setMediaType(fileRequest.getContentType());
        photoReport.setFileSize(file.fileSize());
        photoReport.setFilePath(filePath.toString());

        return photoReport;

    }

    /**
     * /метод создает новый отчет о питомце
     * @param caption текстовое сообщение к фотографии
     */
    private ReportPet createReportPet(Long chatId, String caption, PhotoReport photoReport) {
        ShelterCatUser shelterCatUser = shelterUserService.findCatUser(chatId);
        ShelterDogUser shelterDogUser = shelterUserService.findDogUser(chatId);

        ReportPet reportPet = new ReportPet();
        reportPet.setChatId(chatId);
        if (shelterCatUser != null) {
            reportPet.setShelterCatUser(shelterCatUser);
        } else if (shelterDogUser != null) {
            reportPet.setShelterDogUser(shelterDogUser);
        } else {
            throw new IllegalArgumentException("Владельца с таким chatId не существует:" + chatId);
        }
        reportPet.setDateTime(LocalDateTime.now());
        reportPet.setInfoPet(caption);
        reportPet.setPhotoReport(photoReport);
        return reportPet;
    }
    /**
     * Метод обновляет отчет о питомце.
     * За один день владелец питомца может отправить в БД только один отчет.
     * Если владелец отправляет 2-ой или более отчет в день, то текущий отчет обновляется.
     * @param reportId айди текущего отчета
     * @param caption новый текстовый отчет
     * @return обновленный отчет
     */
    private ReportPet updateReportPet(long reportId, String caption, PhotoReport photoReport) {
        ReportPet reportPet = reportPetRepository.findReportPetById(reportId);

        reportPet.setDateTime(LocalDateTime.now());
        reportPet.setInfoPet(caption);
        reportPet.setPhotoReport(photoReport);
        return reportPet;
    }

    /**
     * Метод загружает фотографию питомца на сервер в папку
     * @param fileData массив байтов, хранящий фотографию
     * @param filePath путь для сохранения фотографии
     * @Throw RuntimeException ошибка при сохранении фото
     *
     */
    private void uploadPhotoToServer(byte[] fileData, Path filePath) {
        try (InputStream is = new ByteArrayInputStream(fileData);
             OutputStream os=Files.newOutputStream(filePath,CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * первая стадия метода отправки отчета. Этот метод отпрвляет пользователю сообщение с просьбой
     * отправить отчет: текст и фото
     *
     * @param chatId      идентификатор чата
     * @param messageText сообщение для пользователя
     */
    public void sendReport(long chatId, String messageText) {
        sendMessageReply(chatId, messageText);
    }

    /**
     * Метод вызывается при отправке отчета пользователем, который
     * не является владельцем питомца
     * @param chatId идентификатор чата
     * @param messageText сообщение пользователю
     */
    public void sendReportWithoutReply(long chatId, String messageText) {
        sendMessage(chatId, messageText);
    }

    /**
     * Метод получает расширение файла из его полного пути
     * @param fileName имя файла
     * @return расширение файла
     */
    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void sendMessageReply(long chatId, String messageText) {
        SendMessage sendMess = new SendMessage(chatId, messageText);
        sendMess.replyMarkup(new ForceReply());
        telegramBot.execute(sendMess);
    }

    public void sendMessage(long chatId, String messageText) {
        SendMessage sendMess = new SendMessage(chatId, messageText);
        telegramBot.execute(sendMess);
    }

    /**
     * метод для волонтера, для отправки усыновителю предупреждения о том, что отчет заполняется плохо
     * @param quality - степень качества заполнения отчета
     */
    public void sendWarningByVolunteer(Long id, boolean quality) {
        ReportPet findReportPet = reportPetRepository.findById(id).get();
        findReportPet.setQuality(quality);
        ReportPet result = reportPetRepository.save(findReportPet);
        if(result.isQuality() == false){
            sendMessage(result.getChatId(), "Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно, как необходимо. Пожалуйста, подойди ответственнее к этому занятию. В противном случае волонтеры приюта будут обязаны самолично проверять условия содержания животного.");
        } else{
            sendMessage(result.getChatId(), "Отчет заполнен качественно. Спасибо.");
        }
    }

    /**
     * Метод выводит список всех отчетов по определенным датам.
     */
    public Collection<ReportPet> getAllReportPet(LocalDate date){
        return reportPetRepository.findReportPetByDateTimeBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    /**
     * Метод возвращает список отчетов по айди владельца
     */
    public Collection<ReportPet> getAllReportPetByUserId(Long userId) {
        ShelterCatUser shelterCatUser = shelterUserService.findCatUserById(userId);
        ShelterDogUser shelterDogUser = shelterUserService.findDogUserById(userId);
        if (shelterCatUser != null) {
            return reportPetRepository.findReportPetByShelterCatUser(shelterCatUser);
        } else if (shelterDogUser != null) {
            return reportPetRepository.findReportPetByShelterDogUser(shelterDogUser);
        } else {
            throw new IllegalArgumentException("Владельца с таким айди не существует");
        }
    }

}
