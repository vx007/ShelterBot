package com.skypro.shelterbot.service;

import com.skypro.shelterbot.exception.EntryNotFoundException;
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
    public Pet add(@NonNull Pet pet) {
        if (!Objects.isNull(pet.getId())) {
            pet.setId(null);
        }
        return petRepository.save(pet);
    }

    public Pet getById(@NonNull Long id) throws EntryNotFoundException {
        return petRepository.findById(id).orElseThrow(() -> new EntryNotFoundException("Pet not found by ID"));
    }

    public List<Pet> getAll() throws EntryNotFoundException {
        if (!petRepository.findAll().isEmpty()) {
            return petRepository.findAll();
        } else {
            throw new EntryNotFoundException("Pets not found");
        }
    }

    @Transactional
    public Pet updateType(@NonNull Long id, PetType type) throws EntryNotFoundException {
        var pet = getById(id);
        pet.setType(type);
        return petRepository.save(pet);
    }

    @Transactional
    public Pet updateName(@NonNull Long id, String name) throws EntryNotFoundException {
        var pet = getById(id);
        pet.setName(name);
        return petRepository.save(pet);
    }

    @Transactional
    public Pet updateAge(@NonNull Long id, Integer age) throws EntryNotFoundException {
        var pet = getById(id);
        pet.setAge(age);
        return petRepository.save(pet);
    }

    @Transactional
    public Pet updateBreed(@NonNull Long id, String breed) throws EntryNotFoundException {
        var pet = getById(id);
        pet.setBreed(breed);
        return petRepository.save(pet);
    }

    @Transactional
    public void remove(@NonNull Long id) throws EntryNotFoundException {
        if (petRepository.existsById(id)) {
            petRepository.deleteById(id);
        } else {
            throw new EntryNotFoundException("Pets not found by ID");
        }
    }
}
