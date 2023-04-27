package com.skypro.shelterbot.service;

import com.skypro.shelterbot.exceptions.PersonDogNotFoundException;
import com.skypro.shelterbot.model.PersonDog;
import com.skypro.shelterbot.repository.PersonDogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PersonDogService {
    private final PersonDogRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(PersonDogService.class);

    public PersonDogService(PersonDogRepository repository) {
        this.repository = repository;
    }


    public PersonDog getById(Long id) {
        logger.info("Был вызван метод для получения хозяина собаки с помощью id={}", id);

        return this.repository.findById(id)
                .orElseThrow(PersonDogNotFoundException::new);
    }


    public PersonDog create(PersonDog personDog) {
        logger.info("Был вызван метод для создания хозяина собаки");

        return this.repository.save(personDog);
    }


    public PersonDog update(PersonDog personDog) {
        logger.info("Был вызван метод для обновления хозяина собаки");

        if (personDog.getId() != null) {
            if (getById(personDog.getId()) != null) {
                return this.repository.save(personDog);
            }
        }
        throw new PersonDogNotFoundException();
    }


    public void removeById(Long id) {
        logger.info("Был вызван метод для удаления хозяина собаки с помощью id={}", id);

        this.repository.deleteById(id);
    }


    public Collection<PersonDog> getAll() {
        logger.info("Был вызван метод для получения всех хозяев собак");

        return this.repository.findAll();
    }


    public Collection<PersonDog> getByChatId(Long chatId) {
        logger.info("Был вызван метод для удаления хозяина собаки с помощью chatId={}", chatId);

        return this.repository.findByChatId(chatId);
    }
}
