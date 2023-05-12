package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User add(@NonNull User user) {
        if (!userRepository.existsByChatId(user.getChatId())) {
            userRepository.save(user);
        }
        return user;
    }

    public User getByChatId(@NonNull Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public User getByPetId(@NonNull Long petId) {
        return userRepository.findByPetId(petId);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void updateName(@NonNull Long chatId, String name) {
        var user = userRepository.findByChatId(chatId);
        user.setName(name);
        userRepository.save(user);
    }

    @Transactional
    public void updatePhone(@NonNull Long chatId, String phone) {
        var user = userRepository.findByChatId(chatId);
        user.setPhone(phone);
        userRepository.save(user);
    }

    @Transactional
    public void updateLastCommand(@NonNull Long chatId, String command) {
        var user = userRepository.findByChatId(chatId);
        user.setLastCommand(command);
        userRepository.save(user);
    }

    @Transactional
    public void remove(@NonNull Long chatId) {
        if (userRepository.existsByChatId(chatId)) {
            userRepository.deleteByChatId(chatId);
        }
    }
}
