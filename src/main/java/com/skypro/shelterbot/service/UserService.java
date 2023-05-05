package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void add(User user) {
        var chatId = user.getChatId();
        if (userRepository.findByChatId(chatId).isEmpty()) {
            userRepository.save(user);
        }
    }

    public Optional<User> getByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public Optional<User> getByPetId(Long petId) {
        return userRepository.findByPetId(petId);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void remove(User user) {
        var chatId = user.getChatId();
        if (userRepository.findByChatId(chatId).isPresent()) {
            userRepository.delete(user);
        }
    }

    @Transactional
    public void remove(Long chatId) {
        if (userRepository.findByChatId(chatId).isPresent()) {
            userRepository.deleteByChatId(chatId);
        }
    }
}
