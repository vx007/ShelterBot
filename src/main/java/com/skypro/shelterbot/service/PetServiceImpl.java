package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Pet;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@Service
public class PetServiceImpl implements PetService {

    private final Map<Long, Pet> pets = new TreeMap<>();
    private static Long idPet = Long.valueOf(1L);

    @Override
    public Pet addPet(Pet pet) {
        pets.put(idPet++, pet);
        return pet;
    }

    @Override
    public Pet getPetById(Long idPet) {
        if(pets.containsKey(idPet)) {
            return pets.get(idPet);
        } else {
            throw new RuntimeException("Такого животного нет!");
        }
    }

    @Override
    public Pet removePetById(Long idPet) {
        return pets.remove(idPet);
    }

    @Override
    public Collection<Pet> getAllPets() {
        return pets.values();
    }

}
