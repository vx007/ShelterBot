package com.skypro.shelterbot.service;

import com.skypro.shelterbot.enums.TrialPeriod;
import com.skypro.shelterbot.exception.OwnerNotFoundException;
import com.skypro.shelterbot.model.ShelterCatUser;
import com.skypro.shelterbot.model.ShelterDogUser;
import com.skypro.shelterbot.repositories.ShelterCatUserRepository;
import com.skypro.shelterbot.repositories.ShelterDogUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.skypro.shelterbot.enums.TrialPeriod.*;


@Service
public class ShelterUserService {
    @Autowired
    private TelegramBot telegramBot;

    private final ShelterCatUserRepository shelterCatUserRepository;
    private final ShelterDogUserRepository shelterDogUserRepository;

    private final String statusCURRENT = "Статус нельзя поменять на CURRENT!";
    private final String statusNOT_PASSED = "Испытательный период не пройден";
    private final String statusSUCCESS_PASSED = "Испытательный период пройден успешно";
    private final String statusEXTENDED_14_DAYS = "Испытательный период продлен на 14 дней";
    private final String statusEXTENDED_30_DAYS = "Испытательный период продлен на 30 дней";
    private final String unCorrectedStatus = "Некорректный новый статус";

    public ShelterUserService(ShelterCatUserRepository shelterCatUserRepository, ShelterDogUserRepository shelterDogUserRepository) {

        this.shelterCatUserRepository = shelterCatUserRepository;
        this.shelterDogUserRepository = shelterDogUserRepository;
    }

    /**
     * Метод позволяет волонтеру добавить владельца кота в бд.
     */
    public ShelterCatUser addCatUser(ShelterCatUser shelterCatUser) {
        return shelterCatUserRepository.save(shelterCatUser);
    }

    /**
     * Метод позволяет волонтеру добавить владельца собак в бд.
     */
    public ShelterDogUser addDogUser(ShelterDogUser shelterDogUser) {
        return shelterDogUserRepository.save(shelterDogUser);
    }

    /**
     * Метод ищет хозяина собаки по его chatId
     */
    public ShelterDogUser findDogUser(long chatId) {
        return shelterDogUserRepository.findShelterDogUserByChatId(chatId);
    }

    /**
     * Метод ищет хозяина кошки по его chatId
     */
    public ShelterCatUser findCatUser(long chatId) {
        return shelterCatUserRepository.findShelterCatUserByChatId(chatId);
    }


    public Collection<ShelterCatUser> allCatUser() {
        return shelterCatUserRepository.findAll();
    }

    public Collection<ShelterDogUser> allDogUser() {
        return shelterDogUserRepository.findAll();
    }

    public ShelterCatUser findCatUserById(Long id) {
        return shelterCatUserRepository.findShelterCatUserById(id);
    }

    public ShelterDogUser findDogUserById(Long id) {
        return shelterDogUserRepository.findShelterDogUserById(id);
    }

    /**
     * Метод возвращает список владельцев собак, у которых завершился испытательный период.
     */
    public List<ShelterDogUser> getDogUserEndTrialPeriod() {
        return allDogUser().stream()
                .filter(shelterDogUser -> shelterDogUser.getEndTrialPeriod().isBefore(LocalDate.now()))
                .filter(shelterDogUser -> shelterDogUser.getTrialPeriod().equals(CURRENT) ||
                        shelterDogUser.getTrialPeriod().equals(EXTENDED_14_DAYS) ||
                        shelterDogUser.getTrialPeriod().equals(EXTENDED_30_DAYS))
                .collect(Collectors.toList());
    }

    /**
     * Метод возвращает список владельцев кошек, у которых завершился испытательный период.
     */
    public List<ShelterCatUser> getCatUserEndTrialPeriod() {
        return allCatUser().stream()
                .filter(shelterCatUser -> shelterCatUser.getEndTrialPeriod().isBefore(LocalDate.now()))
                .filter(shelterCatUser -> shelterCatUser.getTrialPeriod().equals(CURRENT) ||
                        shelterCatUser.getTrialPeriod().equals(EXTENDED_14_DAYS) ||
                        shelterCatUser.getTrialPeriod().equals(EXTENDED_30_DAYS))
                .collect(Collectors.toList());
    }

    /**
     * метод изменяет статус испытательного периода владельца собаки на другой статус.
     * При необходимости изменяет дату окончания испытательного периода
     * Измененный объект сохраняет в БД
     *
     * @param userId идентификтор владельца питомца
     * @param stp новый статус испытательного периода
     * @return Владелец питомца с новым статусом и датой окончания испытательног периода
     * @throws IllegalArgumentException если неверный параметр (например, CURRENT)
     * @throws OwnerNotFoundException   если владелец питомца не найден по его идентификатору
     */
    public ShelterDogUser changeStatusTrialPeriodDog(long userId, TrialPeriod stp) {
        ShelterDogUser shelterDogUser = shelterDogUserRepository.findShelterDogUserById(userId);
        if (shelterDogUser == null) {
            throw new OwnerNotFoundException();
        } else {
            long chatId = shelterDogUser.getChatId();
            LocalDate curentDate = null;

            switch (stp) {
                case CURRENT:
                    throw new IllegalArgumentException(statusCURRENT);
                case SUCCESS_PASSED:
                    shelterDogUser.setTrialPeriod(SUCCESS_PASSED);
                    sendMessage(chatId, statusSUCCESS_PASSED);
                    break;
                case NOT_PASSED:
                    shelterDogUser.setTrialPeriod(NOT_PASSED);
                    sendMessage(chatId, statusNOT_PASSED);
                    break;
                case EXTENDED_14_DAYS:
                    curentDate = shelterDogUser.getEndTrialPeriod();
                    shelterDogUser.setEndTrialPeriod(curentDate.plusDays(14));
                    shelterDogUser.setTrialPeriod(EXTENDED_14_DAYS);
                    sendMessage(chatId, statusEXTENDED_14_DAYS);
                    break;
                case EXTENDED_30_DAYS:
                    curentDate = shelterDogUser.getEndTrialPeriod();
                    shelterDogUser.setEndTrialPeriod(curentDate.plusDays(30));
                    shelterDogUser.setTrialPeriod(EXTENDED_30_DAYS);
                    sendMessage(chatId, statusEXTENDED_30_DAYS);
                    break;
                default:
                    throw new IllegalArgumentException(unCorrectedStatus);
            }
            shelterDogUserRepository.save(shelterDogUser);
        }
        return shelterDogUser;
    }

    /**
     * анологичный метод для кошек
     */
    public ShelterCatUser changeStatusTrialPeriodCat(long userId, TrialPeriod stp) {
        ShelterCatUser shelterCatUser = shelterCatUserRepository.findShelterCatUserById(userId);
        if (shelterCatUser == null) {
            throw new OwnerNotFoundException();
        } else {
            long chatId = shelterCatUser.getChatId();
            LocalDate curentDate = null;

            switch (stp) {
                case CURRENT:
                    throw new IllegalArgumentException(statusCURRENT);
                case SUCCESS_PASSED:
                    shelterCatUser.setTrialPeriod(SUCCESS_PASSED);
                    sendMessage(chatId, statusSUCCESS_PASSED);
                    break;
                case NOT_PASSED:
                    shelterCatUser.setTrialPeriod(NOT_PASSED);
                    sendMessage(chatId, statusNOT_PASSED);
                    break;
                case EXTENDED_14_DAYS:
                    curentDate = shelterCatUser.getEndTrialPeriod();
                    shelterCatUser.setEndTrialPeriod(curentDate.plusDays(14));
                    shelterCatUser.setTrialPeriod(EXTENDED_14_DAYS);
                    sendMessage(chatId, statusEXTENDED_14_DAYS);
                    break;
                case EXTENDED_30_DAYS:
                    curentDate = shelterCatUser.getEndTrialPeriod();
                    shelterCatUser.setEndTrialPeriod(curentDate.plusDays(30));
                    shelterCatUser.setTrialPeriod(EXTENDED_30_DAYS);
                    sendMessage(chatId, statusEXTENDED_30_DAYS);
                    break;
                default:
                    throw new IllegalArgumentException(unCorrectedStatus);
            }
            shelterCatUserRepository.save(shelterCatUser);
        }
        return shelterCatUser;
    }

    /**
     * Метод отправляет сообщение пользователю в чате в телеграме
     */
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
    }
    ////????
//    private void sendMessage(long chatId, String messageText) {
//        SendMessage.SendMessageBuilder sendMess = new SendMessage(chatId, messageText);
//        telegramBot.execute(sendMess);
//    }

}
