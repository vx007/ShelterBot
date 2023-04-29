package com.skypro.shelterbot.repositories;

import com.skypro.shelterbot.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet,Long> {
}
