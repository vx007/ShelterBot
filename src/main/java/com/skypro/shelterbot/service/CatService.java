package com.skypro.shelterbot.service;

import com.skypro.shelterbot.exceptions.CatNotFoundException;
import com.skypro.shelterbot.model.Cat;
import com.skypro.shelterbot.repository.CatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CatService {
    private final CatRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(CatService.class);

    public CatService(CatRepository repository) {
        this.repository = repository;
    }


    public Cat getById(Long id) {
        logger.info("Был вызван метод для получения кота по id={}", id);
        return this.repository.findById(id)
                .orElseThrow(CatNotFoundException::new);
    }

    public Cat create(Cat cat) {
        logger.info("Был вызван метод для создания кота");

        return this.repository.save(cat);
    }

    public Cat update(Cat cat) {
        logger.info("Был вызван метод для обновления кота");

        if (cat.getId() != null) {
            if (getById(cat.getId()) != null) {
                return this.repository.save(cat);
            }
        }
        throw new CatNotFoundException();
    }

    public Collection<Cat> getAll() {
        logger.info("Был вызван метод для получения всех кошек");

        return this.repository.findAll();
    }

    public void removeById(Long id) {
        logger.info("Был вызван метод для удаления кошки с помощью id={}", id);

        this.repository.deleteById(id);
    }
}
