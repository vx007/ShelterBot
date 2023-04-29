package com.skypro.shelterbot.repositories;

import com.skypro.shelterbot.model.ShelterCatUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelterCatUserRepository extends JpaRepository<ShelterCatUserRepository,Long> {

    ShelterCatUser findShelterCatUserByChatId(Long chatId);

    ShelterCatUser findShelterCatUserById(Long id);
}
