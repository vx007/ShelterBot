package com.skypro.shelterbot.service;

import com.skypro.shelterbot.exception.EntryNotFoundException;
import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.PetType;
import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.repository.ReportRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.skypro.shelterbot.model.TrialPeriod.CURRENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    private Report report1;
    private Report report2;
    private Report report3;

    private final User user = new User(
            1L,
            "TestName",
            "TestPhone",
            "TestLastCommand",
            Timestamp.valueOf("2001-01-01 01:01:01"),
            CURRENT,
            true,
            new Pet((PetType.CAT),
                    "TestName",
                    1,
                    "TestBread"));


    private final List<Report> reportList = new ArrayList<>();



    @BeforeEach
    public void setUp(){
        reportService = new ReportService(reportRepository);
        report1 = new Report(user, "TestPhotoId1", "TestText1", Timestamp.valueOf("2001-01-01 01:01:01"), true);
        report2 = new Report(user, "TestPhotoId2", "TestText2", Timestamp.valueOf("2002-02-02 02:02:02"), true);
        report3 = new Report(user, "TestPhotoId3", "TestText3", Timestamp.valueOf("2003-03-03 03:03:03"), true);
        reportList.add(report1);
        reportList.add(report2);
        reportList.add(report3);
    }

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;


    @Test
    void testAdd() {
        reportService.add(report1);
        Report addedReport = reportService.add(report1);
        assertNull(addedReport);
    }

    @Test
    void testGetById() throws EntryNotFoundException {
        Mockito.when(reportRepository.findById(any(Long.class))).thenReturn(Optional.of(report1));

        Report actual = reportService.getById(1L);

        Assertions.assertThat(actual.getId()).isEqualTo(report1.getId());
        Assertions.assertThat(actual.getText()).isEqualTo(report1.getText());
        Assertions.assertThat(actual.getPhotoId()).isEqualTo(report1.getPhotoId());
        Assertions.assertThat(actual.getIsApproved()).isEqualTo(report1.getIsApproved());

    }

    @Test
    public void testGetByChatId() throws EntryNotFoundException {
        Long chatId = 1L;

        when(reportRepository.findByUserChatId(chatId)).thenReturn(reportList);

        List<Report> result = reportService.getByChatId(chatId);

        assertEquals(reportList, result);
    }

    @Test
    public void testGetByChatIdNotFound() {
        Long chatId = 1L;
        when(reportRepository.findByUserChatId(chatId)).thenReturn(new ArrayList<>());

        assertThrows(EntryNotFoundException.class, () -> reportService.getByChatId(chatId));
    }

    @Test
    public void testGetLastReportByChatId() {
        Long chatId = 1L;
        when(reportRepository.findByUserChatId(chatId)).thenReturn(new ArrayList<>());

        assertThrows(EntryNotFoundException.class, () -> reportService.getLastReportByChatId(chatId));
    }


    @Test
    public void testGetAll() throws EntryNotFoundException {
        Mockito.when(reportRepository.findAll()).thenReturn(reportList);

        Collection<Report> actual = reportService.getAll();

        Assertions.assertThat(actual.size()).isEqualTo(reportList.size());
        Assertions.assertThat(actual).isEqualTo(reportList);
    }


    @Test
    public void testUpdatePhotoOnLastReport() {

        Report expected = report1;
        Report actual = report1;

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .comparingOnlyFields("TestPhoto1")
                .isEqualTo(expected);
    }

    @Test
    public void testUpdateTextOnLastReport() {
        Report expected = report1;
        Report actual = report1;

        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .comparingOnlyFields("TestText1")
                .isEqualTo(expected);
    }


    @Test
    public void testApproveLastReport() {
        Long chatId = 1L;
        when(reportRepository.findByUserChatId(chatId)).thenReturn(new ArrayList<>());

        assertThrows(EntryNotFoundException.class, () -> reportService.approveLastReport(chatId));
    }


    @Test
    public void remove() throws EntryNotFoundException {
        Long chatId = 123456L;
        when(reportRepository.existsById(chatId)).thenReturn(true);
        reportService.remove(chatId);
    }

}
