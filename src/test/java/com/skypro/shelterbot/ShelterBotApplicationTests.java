package com.skypro.shelterbot;

import com.skypro.shelterbot.component.TelegramBot;
import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.repository.PetRepository;
import com.skypro.shelterbot.repository.ReportRepository;
import com.skypro.shelterbot.repository.UserRepository;
import com.skypro.shelterbot.service.PetService;
import com.skypro.shelterbot.service.ReportService;
import com.skypro.shelterbot.service.UserService;
import io.restassured.RestAssured;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ShelterBotApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PetRepository petRepository;
    @MockBean
    private ReportRepository reportRepository;

    @SpyBean
    private UserService userService;
    @SpyBean
    private PetService petService;
    @SpyBean
    private ReportService reportService;

    @InjectMocks
    private TelegramBot telegramBot;
//https://api.telegram.org/SashaShelterBot6257060361:AAHT2aRHg3j4nilbitojQx6dC48xLBZVEPw/sendMessage?text=YANSEX_TEST&chat_id=1880942606

    @AfterEach
    void doAny(){
        System.out.println("AFTER");
    }

    @Test
    void succesSendMessage() {
        RestAssured.baseURI = "https://api.telegram.org/SashaShelterBot6257060361:AAHT2aRHg3j4nilbitojQx6dC48xLBZVEPw";
        given()
                .param("text", "rest-assured_TEST")
                .param("chat_id", "1880942606")
                .when()
                .get("/sendMessage")
                .then()
                .statusCode(200);
    }

    @Test
    void unSuccessMessage(){
        RestAssured.baseURI = "https://api.telegram.org/bot5782481564:AAEIqdv8PKRi3mR88EXbImRxn9QwJbPFaQk";
        given()
                .param("text", "rest-assured_TEST")
                .param("chat_id", "362396673")
                .param("parse_mode", "Markdown")
                .when()
                .get("/sendMessage")
                .then()
                .statusCode(400);
    }

    //#https://api.telegram.org/SashaShelterBot6257060361:AAHT2aRHg3j4nilbitojQx6dC48xLBZVEPw/sendMessage?text=YANSEX_TEST&chat_id=1880942606
//    @Test
//    public void saveUserTest() throws Exception {
//        final Long chatId = 1L;
//        final String name = "dsfdsfsd";
//        final String phone = "45656455";
//        final String lastCommand = "/start";
//        final Timestamp registeredAt = new Timestamp(2023-05-06-16-17-26-749683);
//        final List<Report> reports = new ArrayList<>();
//
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("name", name);
//        jsonObject.put("phone", phone);
//
//        User user = new User();
//        user.setChatId(chatId);
//        user.setName(name);
//        user.setPhone(phone);
//        user.setLastCommand(lastCommand);
//        user.setRegisteredAt(registeredAt);
//        user.setReports(reports);
//
//
//        when(userRepository.save(any(User.class))).thenReturn(user);
//        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/users")
//                        .content(jsonObject.toString())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.chatId").value(chatId))
//                .andExpect(jsonPath("$.name").value(name))
//                .andExpect(jsonPath("$.phone").value(phone));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/books/" + chatId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.chatId").value(chatId))
//                .andExpect(jsonPath("$.name").value(name))
//                .andExpect(jsonPath("$.phone").value(phone));
//    }

}