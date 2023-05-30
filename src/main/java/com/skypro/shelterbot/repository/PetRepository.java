package com.skypro.shelterbot.repository;

import com.skypro.shelterbot.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    // Empty :)
}
