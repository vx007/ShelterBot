package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Transactional
    public void add(Pet pet) {
        var id = pet.getId();
        if (petRepository.findById(id).isEmpty()) {
            petRepository.save(pet);
        }
    }

    public Optional<Pet> getById(Long id) {
        return petRepository.findById(id);
    }

    public List<Pet> getAll() {
        return petRepository.findAll();
    }

    @Transactional
    public void remove(Pet pet) {
        var id = pet.getId();
        if (petRepository.findById(id).isPresent()) {
            petRepository.delete(pet);
        }
    }

    @Transactional
    public void remove(Long id) {
        if (petRepository.findById(id).isPresent()) {
            petRepository.deleteById(id);
        }
    }
}
