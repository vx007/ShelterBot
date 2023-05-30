package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.exception.EntryAlreadyExists;
import com.skypro.shelterbot.exception.EntryNotFoundException;
import com.skypro.shelterbot.model.TrialPeriod;
import com.skypro.shelterbot.model.User;

import com.skypro.shelterbot.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

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
    public void create_ValidUser_ShouldReturnNewUser() throws EntryAlreadyExists {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        User user = new User();
        when(userService.add(user)).thenReturn(user);

        ResponseEntity<User> response = userController.create(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void create_UserAlreadyExists_ShouldReturnNotAcceptable() throws EntryAlreadyExists {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        User user = new User();
        when(userService.add(user)).thenThrow(EntryAlreadyExists.class);

        ResponseEntity<User> response = userController.create(user);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void readByChatId_ExistingChatId_ShouldReturnUser() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;
        User user = new User();
        when(userService.getByChatId(chatId)).thenReturn(user);

        ResponseEntity<User> response = userController.readByChatId(chatId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void readByChatId_NonExistingChatId_ShouldReturnNotFound() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;
        when(userService.getByChatId(chatId)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<User> response = userController.readByChatId(chatId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void readByPetId_ExistingPetId_ShouldReturnUser() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long petId = 123;
        User user = new User();
        when(userService.getByPetId(petId)).thenReturn(user);

        ResponseEntity<User> response = userController.readByPetId(petId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void readByPetId_NonExistingPetId_ShouldReturnNotFound() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long petId = 123;
        when(userService.getByPetId(petId)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<User> response = userController.readByPetId(petId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void readAll_ExistingUsers_ShouldReturnListOfUsers() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        when(userService.getAll()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.readAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    public void readAll_NoUsersFound_ShouldReturnNotFound() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        when(userService.getAll()).thenThrow(EntryNotFoundException.class);

        ResponseEntity<List<User>> response = userController.readAll();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void updateName_ExistingChatId_ShouldReturnUpdatedUser() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;
        String newName = "New Name";
        User user = new User();
        when(userService.updateName(chatId, newName)).thenReturn(user);

        ResponseEntity<User> response = userController.updateName(chatId, newName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void updateName_NonExistingChatId_ShouldReturnNotFound() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;
        String newName = "New Name";
        when(userService.updateName(chatId, newName)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<User> response = userController.updateName(chatId, newName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void updatePhone_ExistingChatId_ShouldReturnUpdatedUser() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;
        String newPhone = "1234567890";
        User user = new User();
        when(userService.updatePhone(chatId, newPhone)).thenReturn(user);

        ResponseEntity<User> response = userController.updatePhone(chatId, newPhone);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void updatePhone_NonExistingChatId_ShouldReturnNotFound() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;
        String newPhone = "1234567890";
        when(userService.updatePhone(chatId, newPhone)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<User> response = userController.updatePhone(chatId, newPhone);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void updateLastCommand_ExistingChatId_ShouldReturnUpdatedUser() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;
        String newCommand = "/start";
        User user = new User();
        when(userService.updateLastCommand(chatId, newCommand)).thenReturn(user);

        ResponseEntity<User> response = userController.updateLastCommand(chatId, newCommand);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void updateLastCommand_NonExistingChatId_ShouldReturnNotFound() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;
        String newCommand = "/start";
        when(userService.updateLastCommand(chatId, newCommand)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<User> response = userController.updateLastCommand(chatId, newCommand);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testUpdatePeriod() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        User user = new User();
        user.setChatId(1L);
        user.setName("John Doe");

        when(userService.updatePeriod(1L, TrialPeriod.EXTENDED_14_DAYS)).thenReturn(user);

        ResponseEntity<User> response = userController.updatePeriod(1L, TrialPeriod.EXTENDED_14_DAYS);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());

        verify(userService, times(1)).updatePeriod(1L, TrialPeriod.EXTENDED_14_DAYS);
    }

    @Test
    public void testUpdatePeriod_EntryNotFoundException() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        when(userService.updatePeriod(1L, TrialPeriod.EXTENDED_14_DAYS)).thenThrow(new EntryNotFoundException());

        ResponseEntity<User> response = userController.updatePeriod(1L, TrialPeriod.EXTENDED_14_DAYS);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(userService, times(1)).updatePeriod(1L, TrialPeriod.EXTENDED_14_DAYS);
    }

    @Test
    public void testUpdateVolunteerStatus() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        User user = new User();
        user.setChatId(1L);
        user.setName("John Doe");

        when(userService.updateVolunteerStatus(1L)).thenReturn(user);

        ResponseEntity<User> response = userController.updateVolunteerStatus(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());

        verify(userService, times(1)).updateVolunteerStatus(1L);
    }

    @Test
    public void testUpdateVolunteerStatus_EntryNotFoundException() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        when(userService.updateVolunteerStatus(1L)).thenThrow(new EntryNotFoundException());

        ResponseEntity<User> response = userController.updateVolunteerStatus(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(userService, times(1)).updateVolunteerStatus(1L);
    }

    @Test
    public void updatePet_ExistingChatIdAndPetId_ShouldReturnUpdatedUser() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;
        long petId = 456;
        User user = new User();
        when(userService.updatePet(chatId, petId)).thenReturn(user);

        ResponseEntity<User> response = userController.updatePet(chatId, petId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void updatePet_NonExistingChatIdOrPetId_ShouldReturnNotFound() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;
        long petId = 456;
        when(userService.updatePet(chatId, petId)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<User> response = userController.updatePet(chatId, petId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void delete_ExistingChatId_ShouldReturnOk() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;

        ResponseEntity<User> response = userController.delete(chatId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void delete_NonExistingChatId_ShouldReturnNotFound() throws EntryNotFoundException {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        long chatId = 123;
        doThrow(EntryNotFoundException.class).when(userService).remove(chatId);

        ResponseEntity<User> response = userController.delete(chatId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

}
