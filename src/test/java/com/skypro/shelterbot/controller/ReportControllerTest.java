package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.PetType;
import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.service.ReportService;
import com.skypro.shelterbot.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static liquibase.repackaged.net.sf.jsqlparser.util.validation.metadata.NamedObject.user;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReportController.class)
public class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReportService reportService;
    @MockBean
    private UserService userService;


    @AfterEach
    void doAny(){
        System.out.println("AFTER");
    }

    final String userName = "name";
    final String userPhone = "phone";
    final long userChatId = 2345667L;
    final Timestamp registeredAt = new Timestamp(1L);
    final String lastCommand = "lastCommand";

    final List<Report> reports = new ArrayList<>();

    final long id = 100L;
    final long chatId = 817781679;
    final String photoId = "photoId";
    final String text = "text";
    final LocalDateTime dateTime = LocalDateTime.now();
    final boolean isApproved = true;

    Report report;

    @BeforeEach
    private void init() {
        final Long petId = 1L;
        final String petName = "name";
        final int petAge = 10;
        final PetType petType = PetType.CAT;
        final String petBreed = "british";

        Pet pet = new Pet(petType, petName, petAge, petBreed);
        pet.setId(petId);

        User user = new User(userChatId, userName, userPhone, lastCommand, registeredAt, pet, reports);
    }

    @Test
    public void getAllReportPositiveTest() throws Exception {

        List<Report> reports1 = new ArrayList<>(List.of(report));

        when(reportService.getAll()).thenReturn(reports1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/report/all_reports")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].user").value(user))
                .andExpect(jsonPath("$[0].photoId").value(photoId))
                .andExpect(jsonPath("$[0].text").value(text))
                .andExpect(jsonPath("$[0].dateTime").value(dateTime))
                .andExpect(jsonPath("$[0].isApproved").value(isApproved));

    }

    @Test
    public void getAllReportByUserChatIdWnenUserIdNotFoundTest() throws Exception {
        when(userService.getByChatId(chatId)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/report/user/{id}", userChatId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
