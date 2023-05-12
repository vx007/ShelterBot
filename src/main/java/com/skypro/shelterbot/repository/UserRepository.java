package com.skypro.shelterbot.repository;

import com.skypro.shelterbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByChatId(Long chatId);
    User findByPetId(Long petId);
    boolean existsByChatId(Long chatId);
    void deleteByChatId(Long chatId);
}
