package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.PetType;
import com.skypro.shelterbot.service.PetService;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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
    public void addPetTest() throws Exception {
        final String name = "newName";
        final int age = 2;

        JSONObject petObject = new JSONObject();
        petObject.put("name", name);
        petObject.put("age", age);

        Pet pet = new Pet();
        pet.setAge(age);
        pet.setName(name);

        when(petService.add(any(Pet.class))).thenReturn(pet);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/pets")
                        .content(petObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    public void getAllPetsTest() throws Exception {
        final String name = "newName";
        final int age = 2;
        final String breed = "taksa";


        final String name1 = "name1";
        final int age1 = 3;
        final String breed1 = "ovcharka";

        Pet pet = new Pet(PetType.DOG, "newName", 2, "taksa");
        Pet pet1 = new Pet(PetType.DOG, "name1", 3, "ovcharka");

        JSONObject petObject = new JSONObject();
        petObject.put("name", name);
        petObject.put("age", age);

        when(petService.getAll()).thenReturn(List.of(pet, pet1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/pets/all_pet")
                        .content(petObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void findPetTest() throws Exception{
        final String name = "newName";
        final int age = 2;
        final long id = 1;

        JSONObject petObject = new JSONObject();
        petObject.put("id", id);
        petObject.put("name", name);
        petObject.put("age", age);

        Pet pet = new Pet();
        pet.setId(id);
        pet.setAge(age);
        pet.setName(name);

        when(petService.getById(id)).thenReturn(pet);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/pets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }
}
