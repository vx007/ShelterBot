package com.skypro.shelterbot.repositories;

import com.skypro.shelterbot.model.ShelterDogUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelterDogUserRepository extends JpaRepository<ShelterDogUserRepository,Long> {

    ShelterDogUser findShelterDogUserByChatId(Long chatId);

    ShelterDogUser findShelterDogUserById(Long id);
}