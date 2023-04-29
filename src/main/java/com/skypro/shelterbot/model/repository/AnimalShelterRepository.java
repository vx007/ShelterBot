package com.skypro.shelterbot.model.repository;

import com.skypro.shelterbot.model.AnimalShelter;
import org.springframework.data.repository.CrudRepository;

public interface AnimalShelterRepository extends CrudRepository<AnimalShelter, Integer> {
}
