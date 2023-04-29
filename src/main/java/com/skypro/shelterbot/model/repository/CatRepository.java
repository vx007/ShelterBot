package com.skypro.shelterbot.model.repository;

import com.skypro.shelterbot.model.Cat;
import org.springframework.data.repository.CrudRepository;

public interface CatRepository extends CrudRepository<Cat, Integer> {
}
