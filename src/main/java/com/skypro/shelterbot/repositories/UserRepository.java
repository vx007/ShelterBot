package com.skypro.shelterbot.repositories;

import com.skypro.shelterbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByChatId(Long chatId);
    Collection<User> findUserByShelter(String shelter);
}
