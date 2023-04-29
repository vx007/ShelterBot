package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.repositories.PetRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PetService {
     private PetRepository petRepository;
    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet addPet(Pet pet){
        return petRepository.save(pet);
    }


    public Pet findPet(Long id){
        return petRepository.findById(id).orElse(null);
    }

    public Collection<Pet> getAllPet(){
        return petRepository.findAll();
    }
}
