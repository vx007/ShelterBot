package com.skypro.shelterbot.service;

import com.skypro.shelterbot.exceptions.DogNotFoundException;
import com.skypro.shelterbot.model.Dog;
import com.skypro.shelterbot.repository.DogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DogService {
    private final DogRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(DogService.class);

    public DogService(DogRepository dogRepository) {
        this.repository = dogRepository;
    }


    public Dog getById(Long id) {
        logger.info("Был вызван метод для получения собаки по id={}", id);
        return this.repository.findById(id)
                .orElseThrow(DogNotFoundException::new);
    }


    public Dog create(Dog dog) {
        logger.info("Был вызван метод для создания собаки");
        return this.repository.save(dog);
    }


    public Dog update(Dog dog) {
        logger.info("Был вызван метод для обновления собаки");
        if (dog.getId() != null) {
            if (getById(dog.getId()) != null) {
                return this.repository.save(dog);
            }
        }
        throw new DogNotFoundException();
    }

    public Collection<Dog> getAll() {
        logger.info("Был вызван метод для получения всех собак");

        return this.repository.findAll();
    }


    public void removeById(Long id) {
        logger.info("Был вызван метод для удаления собаки с помощью id={}", id);

        this.repository.deleteById(id);
    }
}
