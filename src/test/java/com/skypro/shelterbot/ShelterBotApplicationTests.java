package com.skypro.shelterbot;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShelterBotApplicationTests {

    @AfterEach
    public void clean(){

    }

    @Test
    void contextLoads() {
    }
}
