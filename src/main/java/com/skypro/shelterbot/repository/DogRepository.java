package com.skypro.shelterbot.repository;

import com.skypro.shelterbot.model.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long>{
}
