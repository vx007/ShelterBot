package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Transactional
    public Pet add(@NotNull Pet pet) {
        petRepository.save(pet);
        return pet;
    }

    public Pet getById(Long id) {
        return petRepository.findById(id).orElseThrow();
    }

    public List<Pet> getAll() {
        return petRepository.findAll();
    }

    @Transactional
    public void remove(@NotNull Long id) {
        petRepository.deleteById(id);
    }
}
