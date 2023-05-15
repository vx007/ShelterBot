package com.skypro.shelterbot.service;

import com.skypro.shelterbot.exception.EntryAlreadyExists;
import com.skypro.shelterbot.exception.EntryNotFoundException;
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
    public User add(@NonNull User user) throws EntryAlreadyExists {
        if (!userRepository.existsByChatId(user.getChatId())) {
            return userRepository.save(user);
        } else {
            throw new EntryAlreadyExists("User already exists");
        }
    }

    public User getByChatId(@NonNull Long chatId) throws EntryNotFoundException {
        return userRepository.findByChatId(chatId).orElseThrow(() -> new EntryNotFoundException("User not found by chatID"));
    }

    public User getByPetId(@NonNull Long petId) throws EntryNotFoundException {
        return userRepository.findByPetId(petId).orElseThrow(() -> new EntryNotFoundException("User not found by petID"));
    }

    public List<User> getAll() throws EntryNotFoundException {
        if (!userRepository.findAll().isEmpty()){
            return userRepository.findAll();
        } else {
            throw new EntryNotFoundException("Users not found");
        }
    }

    @Transactional
    public User updateName(@NonNull Long chatId, String name) throws EntryNotFoundException {
        var user = getByChatId(chatId);
        user.setName(name);
        return userRepository.save(user);
    }

    @Transactional
    public User updatePhone(@NonNull Long chatId, String phone) throws EntryNotFoundException {
        var user = getByChatId(chatId);
        user.setPhone(phone);
        return userRepository.save(user);
    }

    @Transactional
    public User updateLastCommand(@NonNull Long chatId, String command) throws EntryNotFoundException {
        var user = getByChatId(chatId);
        user.setLastCommand(command);
        return userRepository.save(user);
    }

    @Transactional
    public void remove(@NonNull Long chatId) throws EntryNotFoundException {
        if (userRepository.existsByChatId(chatId)) {
            userRepository.deleteByChatId(chatId);
        } else {
            throw new EntryNotFoundException("User not found by chatID");
        }
    }
}
