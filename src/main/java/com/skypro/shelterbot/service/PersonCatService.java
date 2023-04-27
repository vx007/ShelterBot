package com.skypro.shelterbot.service;

import com.skypro.shelterbot.exceptions.PersonCatNotFoundException;
import com.skypro.shelterbot.model.PersonCat;
import com.skypro.shelterbot.repository.PersonCatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PersonCatService {


    private final PersonCatRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(PersonCatService.class);

    public PersonCatService(PersonCatRepository repository) {
        this.repository = repository;
    }


    public PersonCat getById(Long id) {
        logger.info("Был вызван метод для получения хозяина кошки с помощью id={}", id);

        return this.repository.findById(id)
                .orElseThrow(PersonCatNotFoundException::new);
    }

    public PersonCat create(PersonCat personCat) {
        logger.info("Был вызван метод для создания хозяина кошки");

        return this.repository.save(personCat);
    }

    public PersonCat update(PersonCat personCat) {
        logger.info("Был вызван метод для обновления хозяина кошки");

        if (personCat.getId() != null) {
            if (getById(personCat.getId()) != null) {
                return repository.save(personCat);
            }
        }
        throw new PersonCatNotFoundException();
    }

    public void removeById(Long id) {
        logger.info("Был вызван метод для удаления хозяина кошки с помощью id={}", id);

        this.repository.deleteById(id);
    }

    public Collection<PersonCat> getAll() {
        logger.info("Был вызван метод для получения всех хозяев кошек");

        return this.repository.findAll();
    }

    public Collection<PersonCat> getByChatId(Long chatId) {
        logger.info("Был вызван метод для удаления хозяина кошки по  chatId={}", chatId);

        return this.repository.findByChatId(chatId);
    }
}
