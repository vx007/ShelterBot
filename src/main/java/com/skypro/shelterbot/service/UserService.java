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
            return userRepository.save(user);
        } else {
            return null;
        }
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

    public User updateName(@NonNull Long chatId, String name) {
        var user = userRepository.findByChatId(chatId);
        user.setName(name);
        return userRepository.save(user);
    }

    @Transactional

    public User updatePhone(@NonNull Long chatId, String phone) {
        var user = userRepository.findByChatId(chatId);
        user.setPhone(phone);
        return userRepository.save(user);
    }

    @Transactional

    public User updateLastCommand(@NonNull Long chatId, String command) {
        var user = userRepository.findByChatId(chatId);
        user.setLastCommand(command);
        return userRepository.save(user);
    }

    @Transactional
    public void remove(@NonNull Long chatId) {
        if (userRepository.existsByChatId(chatId)) {
            userRepository.deleteByChatId(chatId);
        }
    }

}
