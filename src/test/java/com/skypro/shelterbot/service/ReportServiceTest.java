package com.skypro.shelterbot.service;

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

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    private Report report1;
    private Report report2;
    private Report report3;

    private User user = new User(
            1L,
            "TestName",
            "TestPhone",
            "TestLastCommand",
            Timestamp.valueOf("2001-01-01 01:01:01"),
            new Pet((PetType.CAT),
                    "TestName",
                    1,
                    "TestBread"),
            new ArrayList<>());

     Pet pet = new Pet(PetType.CAT, "TestName", 1, "TestBread");

    private List<Report> reportList = new ArrayList<>();



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
    void add() {
    }

    @Test
    void getById() {
        Mockito.when(reportRepository.findById(any(Long.class))).thenReturn(Optional.of(report1));

        Report actual = reportService.getById(1L);

        Assertions.assertThat(actual.getId()).isEqualTo(report1.getId());
        Assertions.assertThat(actual.getText()).isEqualTo(report1.getText());
        Assertions.assertThat(actual.getPhotoId()).isEqualTo(report1.getPhotoId());
        Assertions.assertThat(actual.getIsApproved()).isEqualTo(report1.getIsApproved());

    }

//    @Test
//    void getByChatId() {
//        Mockito.when(reportRepository.getReferenceById(any(Long.class))).thenReturn(report3);
//
//        Collection<Report> actual = reportService.getByChatId(1L);
//
//        Assertions.assertThat(actual.size()).isEqualTo(reportList.size());
//        Assertions.assertThat(actual).isEqualTo(reportList);
//    }

    @Test
    void getLastReportByChatId() {
    }

    @Test
    public void getAll() {
        Mockito.when(reportRepository.findAll()).thenReturn(reportList);

        Collection<Report> actual = reportService.getAll();

        Assertions.assertThat(actual.size()).isEqualTo(reportList.size());
        Assertions.assertThat(actual).isEqualTo(reportList);
    }

    @Test
    public void updatePhotoOnLastReport() {
        Mockito.when(reportRepository.findById(any(Long.class))).thenReturn(Optional.of(report2));
        Mockito.when(reportRepository.save(any(Report.class))).thenReturn(report2);

        Report actual = reportService.updatePhotoOnLastReport(1L, "TestPhotoId");

        Assertions.assertThat(actual.getPhotoId()).isEqualTo(report2.getPhotoId());
    }

    @Test
    public void updateTextOnLastReport() {
        Mockito.when(reportRepository.findById(any(Long.class))).thenReturn(Optional.of(report3));
        Mockito.when(reportRepository.save(any(Report.class))).thenReturn(report3);

        Report actual = reportService.updateTextOnLastReport(1L, "TestText");

        Assertions.assertThat(actual.getText()).isEqualTo(report3.getText());
    }

    @Test
    public void approveLastReport() {
    }

    @Test
    public void remove() {
    }
}