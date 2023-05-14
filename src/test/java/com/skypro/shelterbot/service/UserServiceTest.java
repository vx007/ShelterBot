package com.skypro.shelterbot.service;

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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private User user1;
    private User user2;
    private User user3;

    private final List<User> userList = new ArrayList<>();

    @BeforeEach
    public void setUp(){
        userService = new UserService(userRepository);
        user1 = new User(1L,
                "TestName1",
                "TestPhone1",
                "TestLastCommand1",
                Timestamp.valueOf("2001-01-01 01:01:01"),
                new Pet((PetType.CAT),
                        "TestName1",
                        1,
                        "TestBread1"),
                new ArrayList<>());

        user2 = new User(2L,
                "TestName2",
                "TestPhone2",
                "TestLastCommand2",
                Timestamp.valueOf("2002-02-02 02:02:02"),
                new Pet((PetType.CAT),
                        "TestName2",
                        2,
                        "TestBread2"),
                new ArrayList<>());

        user3 = new User(3L,
                "TestName3",
                "TestPhone3",
                "TestLastCommand3",
                Timestamp.valueOf("2003-03-03 03:03:03"),
                new Pet((PetType.CAT),
                        "TestName3",
                        3,
                        "TestBread3"),
                new ArrayList<>());

       userList.add(user1);
       userList.add(user2);
       userList.add(user3);
    }

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private UserService userService;

    @Test
    public void add() {
        userService.add(user1);
        User addedUser = userService.add(user2);
        assertNull(addedUser);
    }

    @Test
    public void getAll() {
        Mockito.when(userRepository.findAll()).thenReturn(userList);

        Collection<User> actual = userService.getAll();

        Assertions.assertThat(actual.size()).isEqualTo(userList.size());
        Assertions.assertThat(actual).isEqualTo(userList);
    }


       @Test
    public void updateName() {

           User expected = user1;
           User actual = user1;

           Assertions.assertThat(actual)
                   .usingRecursiveComparison()
                   .comparingOnlyFields("TestName1")
                   .isEqualTo(expected);
    }

    @Test
   public void updatePhone() {
        User expected = user2;
        User actual = user2;

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .comparingOnlyFields("TestPhone2")
                .isEqualTo(expected);
    }

    @Test
   public void updateLastCommand() {
        User expected = user3;
        User actual = user3;

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .comparingOnlyFields("TestLastCommand3")
                .isEqualTo(expected);
    }

    @Test
    public void remove() {
        user1.setChatId(123456789L);
        userRepository.save(user1);

        userService.remove(123456789L);

        assertFalse(userRepository.existsByChatId(123456789L));
    }
}