package com.skypro.shelterbot.service;

import com.skypro.shelterbot.enums.WhichPet;
import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.repositories.PetRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {
    @InjectMocks
    private PetService petService;

    @Mock
    private PetRepository petRepository;

    private Pet pet1;
    private Pet pet2;
    private Pet pet3;

    private List<Pet> petList = new ArrayList<>();

    @BeforeEach
    public void setUp(){
        petService = new PetService(petRepository);
        pet1 = new Pet("name", 3, WhichPet.DOG, "ovcharka");
        pet2 = new Pet("name1", 3, WhichPet.DOG, "ovcharka");
        pet3 = new Pet("name2", 3, WhichPet.DOG, "ovcharka");
        petList.add(pet1);
        petList.add(pet2);
        petList.add(pet3);
    }

    @Test
    public void addPetTest(){
        Mockito.when(petRepository.save(pet1)).thenReturn(pet1);
        Assertions.assertEquals(pet1, petService.addPet(pet1));
    }

    @Test
    public void findPetTest(){
        Mockito.when(petRepository.findById(pet1.getId())).thenReturn(Optional.ofNullable(pet1));
        Assertions.assertEquals(pet1, petService.findPet(pet1.getId()));
    }

    @Test
    public void getAllPetTest(){
        Mockito.when(petRepository.findAll()).thenReturn(petList);
        Assertions.assertEquals(petList, petService.getAllPet());
    }
}
