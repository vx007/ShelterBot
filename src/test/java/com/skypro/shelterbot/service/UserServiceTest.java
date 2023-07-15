package com.skypro.shelterbot.service;

import com.skypro.shelterbot.exception.EntryAlreadyExists;
import com.skypro.shelterbot.exception.EntryNotFoundException;
import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.PetType;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.*;

import static com.skypro.shelterbot.model.TrialPeriod.CURRENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private User user1;
    private User user2;
    private User user3;

    private final List<User> userList = new ArrayList<>();

    @BeforeEach
    public void setUp(){
        user1 = new User(1L,
                "TestName1",
                "TestPhone1",
                "TestLastCommand1",
                Timestamp.valueOf("2001-01-01 01:01:01"),
                CURRENT,
                true,
                new Pet((PetType.CAT),
                "TestName1",
                1,
                "TestBread1"));

        user2 = new User(2L,
                "TestName2",
                "TestPhone2",
                "TestLastCommand2",
                Timestamp.valueOf("2002-02-02 02:02:02"),
                CURRENT,
                true,
                new Pet((PetType.CAT),
                        "TestName2",
                        2,
                        "TestBread2"));

        user3 = new User(3L,
                "TestName3",
                "TestPhone3",
                "TestLastCommand3",
                Timestamp.valueOf("2003-03-03 03:03:03"),
                CURRENT,
                true,
                new Pet((PetType.CAT),
                        "TestName3",
                        3,
                        "TestBread3"));

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
    }

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private UserService userService;

    @Test
    public void testAdd() throws EntryAlreadyExists {
        userService.add(user1);
        User addedUser = userService.add(user2);
        assertNull(addedUser);
    }

    @Test
    public void testGetByChatId() throws EntryNotFoundException {
        Long chatId = 1L;

        when(userRepository.findByChatId(chatId)).thenReturn(Optional.of(user1));

        User result = userService.getByChatId(chatId);

        assertEquals(user1, result);
        verify(userRepository, times(1)).findByChatId(chatId);
    }

    @Test
    public void testGetByChatIdNotFound() {
        Long chatId = 1L;
        when(userRepository.findByChatId(chatId)).thenReturn(Optional.empty());

        assertThrows(EntryNotFoundException.class, () -> userService.getByChatId(chatId));
    }

    @Test
    public void testGetByPetId() throws EntryNotFoundException {
        Long petId = 1L;

        when(userRepository.findByPetId(petId)).thenReturn(Optional.of(user1));

        User result = userService.getByPetId(petId);

        assertEquals(user1, result);
        verify(userRepository, times(1)).findByPetId(petId);
    }

    @Test
    public void testGetByPetIdNotFound() {
        Long petId = 1L;
        when(userRepository.findByPetId(petId)).thenReturn(Optional.empty());

        assertThrows(EntryNotFoundException.class, () -> userService.getByPetId(petId));
    }

    @Test
    public void testGetAll() throws EntryNotFoundException {
        Mockito.when(userRepository.findAll()).thenReturn(userList);

        Collection<User> actual = userService.getAll();

        Assertions.assertThat(actual.size()).isEqualTo(userList.size());
        Assertions.assertThat(actual).isEqualTo(userList);
    }



    @Test
    public void testUpdateName() {

        User expected = user1;
        User actual = user1;

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .comparingOnlyFields("TestName1")
                .isEqualTo(expected);
    }

    @Test
    public void testUpdatePhone() {
        User expected = user2;
        User actual = user2;

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .comparingOnlyFields("TestPhone2")
                .isEqualTo(expected);
    }

    @Test
    public void testUpdateLastCommand() {
        User expected = user3;
        User actual = user3;

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .comparingOnlyFields("TestLastCommand3")
                .isEqualTo(expected);
    }

    @Test
    public void testUpdatePeriod() {
        User expected = user1;
        User actual = user1;

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .comparingOnlyFields("TestPeriod")
                .isEqualTo(expected);
    }

    @Test
    public void testUpdateVolunteerStatus() {
        User expected = user1;
        User actual = user1;

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .comparingOnlyFields("TestVolunteerStatus")
                .isEqualTo(expected);
    }

    @Test
    public void testUpdatePet() {
        User expected = user1;
        User actual = user1;

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .comparingOnlyFields("TestPet")
                .isEqualTo(expected);
    }

    @Test
    public void testRemove() throws EntryNotFoundException {
        Long chatId = 123456L;
        when(userRepository.existsByChatId(chatId)).thenReturn(true);
        userService.remove(chatId);
    }
}
