package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void add(@NotNull User user) {
        userRepository.save(user);
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
    public void remove(@NotNull User user) {
        userRepository.delete(user);
    }

    @Transactional
    public void remove(@NotNull Long chatId) {
        userRepository.deleteByChatId(chatId);
    }

    public Collection<User> findUserByPet(Pet pet) {
        return userRepository.findUserByPet(pet);
    }
}
