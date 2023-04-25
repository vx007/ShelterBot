package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Pet;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface PetService {
    Pet addPet(Pet pet);

    Pet getPetById(Long idPet);

    Pet removePetById(Long idPet);

    Collection<Pet> getAllPets();
}
