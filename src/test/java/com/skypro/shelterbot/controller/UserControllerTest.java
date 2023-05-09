package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.model.User;

import com.skypro.shelterbot.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @AfterEach
    void doAny(){
        System.out.println("AFTER");
    }


    @Test
    public void getAllUsersPositiveTest() throws Exception{

        final Long chatId = 232L;
        final String name = "Diana";
        final String phone = "userPhone";
        final String lastCommand = "lastCommand";
        final Timestamp registeredAt = new Timestamp(1L);

        Pet pet = new Pet();
        pet.setAge(3);
        pet.setName("Toti");

        final List<Report> reports = new ArrayList<>();

        User user = new User(232L, "Diana", "userPhone", "lastCommand", registeredAt, pet, reports);
        List<User> users = new ArrayList<>(List.of(user));

        Mockito.when(userService.getAll()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/all_users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].chatId").value(chatId))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].phone").value(phone))
                .andExpect(jsonPath("$[0].lastCommand").value(lastCommand))
                .andExpect(jsonPath("$[0].registeredAt").value(registeredAt))
                .andExpect(jsonPath("$[0].pet").value(pet))
                .andExpect(jsonPath("$[0].reports").value(reports));

    }

    @Test
    public void getAllUsersTestWhenReturnEmptyList() throws Exception{

        Mockito.when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/all_users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findUserByPetPositiveTest() throws Exception{

        final Long chatId = 232L;
        final String name = "Diana";
        final String phone = "userPhone";
        final String lastCommand = "lastCommand";
        final Timestamp registeredAt = new Timestamp(1L);

        Pet pet = new Pet();
        pet.setAge(3);
        pet.setName("Toti");

        final List<Report> reports = new ArrayList<>();

        User user = new User(232L, "Diana", "userPhone", "lastCommand", registeredAt, pet, reports);
        List<User> users = new ArrayList<>(List.of(user));

        Mockito.when(userService.findUserByPet(pet)).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/pet?pet={pet}", pet)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].chatId").value(chatId))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].phone").value(phone))
                .andExpect(jsonPath("$[0].lastCommand").value(lastCommand))
                .andExpect(jsonPath("$[0].registeredAt").value(registeredAt))
                .andExpect(jsonPath("$[0].pet").value(pet))
                .andExpect(jsonPath("$[0].reports").value(reports));

    }
}
