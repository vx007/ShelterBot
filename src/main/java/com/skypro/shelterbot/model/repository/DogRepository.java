package com.skypro.shelterbot.model.repository;

import com.skypro.shelterbot.model.Dog;
import org.springframework.data.repository.CrudRepository;

public interface DogRepository extends CrudRepository<Dog, Integer> {
}
