package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.exception.EntryNotFoundException;
import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.PetType;
import com.skypro.shelterbot.service.PetService;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PetController.class)
public class PetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @AfterEach
    void doAny(){
        System.out.println("AFTER");
    }

    @Test
    void create_ShouldReturnCreatedPet() {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        Pet inputPet = new Pet();
        Pet expectedPet = new Pet();
        when(petService.add(inputPet)).thenReturn(expectedPet);

        ResponseEntity<Pet> response = petController.create(inputPet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPet, response.getBody());
    }

    @Test
    void readById_ExistingId_ShouldReturnPet() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        long existingId = 123;
        Pet expectedPet = new Pet();
        when(petService.getById(existingId)).thenReturn(expectedPet);

        ResponseEntity<Pet> response = petController.readById(existingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPet, response.getBody());
    }

    @Test
    void readById_NonExistingId_ShouldReturnNotFound() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        long nonExistingId = 456;
        when(petService.getById(nonExistingId)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<Pet> response = petController.readById(nonExistingId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void readAll_PetsExist_ShouldReturnListOfPets() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        List<Pet> expectedPets = new ArrayList<>();
        expectedPets.add(new Pet());
        expectedPets.add(new Pet());
        when(petService.getAll()).thenReturn(expectedPets);

        ResponseEntity<List<Pet>> response = petController.readAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPets, response.getBody());
    }

    @Test
    void readAll_NoPetsExist_ShouldReturnNotFound() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        when(petService.getAll()).thenThrow(EntryNotFoundException.class);

        ResponseEntity<List<Pet>> response = petController.readAll();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateType_ExistingId_ShouldReturnUpdatedPet() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        long existingId = 123;
        PetType newType = PetType.CAT;

        Pet expectedPet = new Pet();
        when(petService.updateType(existingId, newType)).thenReturn(expectedPet);

        ResponseEntity<Pet> response = petController.updateType(existingId, newType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPet, response.getBody());
    }

    @Test
    void updateType_NonExistingId_ShouldReturnNotFound() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        long nonExistingId = 456;
        PetType newType = PetType.CAT;

        when(petService.updateType(nonExistingId, newType)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<Pet> response = petController.updateType(nonExistingId, newType);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateName_ExistingId_ShouldReturnUpdatedPet() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        long existingId = 123;
        String newName = "Fluffy";

        Pet expectedPet = new Pet();
        when(petService.updateName(existingId, newName)).thenReturn(expectedPet);

        ResponseEntity<Pet> response = petController.updateName(existingId, newName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPet, response.getBody());
    }

    @Test
    void updateName_NonExistingId_ShouldReturnNotFound() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        long nonExistingId = 456;
        String newName = "Fluffy";

        when(petService.updateName(nonExistingId, newName)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<Pet> response = petController.updateName(nonExistingId, newName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateAge_ExistingId_ShouldReturnUpdatedPet() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        long existingId = 123;
        int newAge = 3;

        Pet expectedPet = new Pet();
        when(petService.updateAge(existingId, newAge)).thenReturn(expectedPet);

        ResponseEntity<Pet> response = petController.updateAge(existingId, newAge);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPet, response.getBody());
    }

    @Test
    void updateAge_NonExistingId_ShouldReturnNotFound() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        long nonExistingId = 456;
        int newAge = 3;

        when(petService.updateAge(nonExistingId, newAge)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<Pet> response = petController.updateAge(nonExistingId, newAge);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateBreed_ExistingId_ShouldReturnUpdatedPet() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        long existingId = 123;
        String newBreed = "Labrador Retriever";

        Pet expectedPet = new Pet();
        when(petService.updateBreed(existingId, newBreed)).thenReturn(expectedPet);

        ResponseEntity<Pet> response = petController.updateBreed(existingId, newBreed);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPet, response.getBody());
    }

    @Test
    void updateBreed_NonExistingId_ShouldReturnNotFound() throws EntryNotFoundException {
        PetService petService = mock(PetService.class);
        PetController petController = new PetController(petService);

        long nonExistingId = 456;
        String newBreed = "Labrador Retriever";

        when(petService.updateBreed(nonExistingId, newBreed)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<Pet> response = petController.updateBreed(nonExistingId, newBreed);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

}
