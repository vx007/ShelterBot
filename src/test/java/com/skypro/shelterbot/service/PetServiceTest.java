package com.skypro.shelterbot.service;

import com.skypro.shelterbot.exception.EntryNotFoundException;
import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.PetType;
import com.skypro.shelterbot.repository.PetRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    private Pet pet1;
    private Pet pet2;
    private Pet pet3;

    private final List<Pet> petList = new ArrayList<>();


    @BeforeEach
    public void setUp(){
        petService = new PetService(petRepository);
        pet1 = new Pet(PetType.CAT, "TestName1", 1, "TestBread1" );
        pet2 = new Pet(PetType.CAT, "TestName2", 2, "TestBread2" );
        pet3 = new Pet(PetType.CAT, "TestName3", 3, "TestBread3" );
        petList.add(pet1);
        petList.add(pet2);
        petList.add(pet3);
    }

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    @Test
    void testAdd() {
        petService.add(pet1);
        Pet addedPet = petService.add(pet1);
        assertNull(addedPet);
    }

    @Test
    void testGetById() throws EntryNotFoundException {
        Mockito.when(petRepository.findById(any(Long.class))).thenReturn(Optional.of(pet1));

        Pet actual = petService.getById(1L);

        Assertions.assertThat(actual.getType()).isEqualTo(pet1.getType());
        Assertions.assertThat(actual.getName()).isEqualTo(pet1.getName());
        Assertions.assertThat(actual.getAge()).isEqualTo(pet1.getAge());
        Assertions.assertThat(actual.getBreed()).isEqualTo(pet1.getBreed());

    }

    @Test
    public void testGetByIdNotFound() {
        Long petId = 1L;
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        assertThrows(EntryNotFoundException.class, () -> petService.getById(petId));
    }

    @Test
    void testGetAll() throws EntryNotFoundException {
        Mockito.when(petRepository.findAll()).thenReturn(petList);

        Collection<Pet> actual = petService.getAll();

        Assertions.assertThat(actual.size()).isEqualTo(petList.size());
        Assertions.assertThat(actual).isEqualTo(petList);
    }


    @Test
    void testUpdateType() throws EntryNotFoundException {
        Mockito.when(petRepository.findById(any(Long.class))).thenReturn(Optional.of(pet2));
        Mockito.when(petRepository.save(any(Pet.class))).thenReturn(pet2);

        Pet actual = petService.updateType(1L, PetType.CAT);

        Assertions.assertThat(actual.getType()).isEqualTo(pet2.getType());
    }

    @Test
    void testUpdateName() throws EntryNotFoundException {
        Mockito.when(petRepository.findById(any(Long.class))).thenReturn(Optional.of(pet3));
        Mockito.when(petRepository.save(any(Pet.class))).thenReturn(pet3);

        Pet actual = petService.updateName(1L, "TestName");

        Assertions.assertThat(actual.getName()).isEqualTo(pet3.getName());
    }

    @Test
    void testUpdateAge() throws EntryNotFoundException {
        Mockito.when(petRepository.findById(any(Long.class))).thenReturn(Optional.of(pet3));
        Mockito.when(petRepository.save(any(Pet.class))).thenReturn(pet3);

        Pet actual = petService.updateAge(1L, 1);

        Assertions.assertThat(actual.getAge()).isEqualTo(pet3.getAge());
    }

    @Test
    void testUpdateBreed() throws EntryNotFoundException {
        Mockito.when(petRepository.findById(any(Long.class))).thenReturn(Optional.of(pet3));
        Mockito.when(petRepository.save(any(Pet.class))).thenReturn(pet3);

        Pet actual = petService.updateBreed(1L, "TestBread");

        Assertions.assertThat(actual.getBreed()).isEqualTo(pet3.getBreed());
    }

    @Test
    public void testRemove() throws EntryNotFoundException {
        Long chatId = 123456L;
        when(petRepository.existsById(chatId)).thenReturn(true);
        petService.remove(chatId);
    }
}
