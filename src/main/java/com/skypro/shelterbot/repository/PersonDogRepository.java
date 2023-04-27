package com.skypro.shelterbot.repository;

import com.skypro.shelterbot.model.PersonDog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface PersonDogRepository extends JpaRepository<PersonDog, Long>  {
    Set<PersonDog> findByChatId(Long chatId);
}
