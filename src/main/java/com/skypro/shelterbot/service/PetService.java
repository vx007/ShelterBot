package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.PetType;
import com.skypro.shelterbot.repository.PetRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class PetService {
    private final PetRepository petRepository;

    @Transactional
    public void add(@NonNull Pet pet) {
        if (!Objects.isNull(pet.getId())) {
            pet.setId(null);
        }
        petRepository.save(pet);
    }

    public Pet getById(@NonNull Long id) {
        return petRepository.findById(id).orElseThrow();
    }

    public List<Pet> getAll() {
        return petRepository.findAll();
    }

    @Transactional
    public void updateType(@NonNull Long id, PetType type) {
        var pet = getById(id);
        pet.setType(type);
        petRepository.save(pet);
    }

    @Transactional
    public void updateName(@NonNull Long id, String name) {
        var pet = getById(id);
        pet.setName(name);
        petRepository.save(pet);
    }

    @Transactional
    public void updateAge(@NonNull Long id, Integer age) {
        var pet = getById(id);
        pet.setAge(age);
        petRepository.save(pet);
    }

    @Transactional
    public void updateBreed(@NonNull Long id, String breed) {
        var pet = getById(id);
        pet.setBreed(breed);
        petRepository.save(pet);
    }

    @Transactional
    public void remove(@NonNull Long id) {
        if (petRepository.existsById(id)) {
            petRepository.deleteById(id);
        }
    }
}
