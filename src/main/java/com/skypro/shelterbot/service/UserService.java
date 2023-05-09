package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @CachePut(value = "users", key = "#user.chatId")
    public void add(@NotNull User user) {
        userRepository.save(user);
    }

    @Cacheable("users")
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
    @CacheEvict("users")
    public void remove(@NotNull User user) {
        userRepository.delete(user);
    }

    @Transactional
    @CacheEvict("users")
    public void remove(@NotNull Long chatId) {
        userRepository.deleteByChatId(chatId);
    }
}
