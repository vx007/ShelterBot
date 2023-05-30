package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.exception.EntryNotFoundException;
import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.service.ReportService;
import com.skypro.shelterbot.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    void create_ShouldReturnCreatedReport() {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        Report report = new Report();

        Report createdReport = new Report();
        when(reportService.add(report)).thenReturn(createdReport);

        ResponseEntity<Report> response = reportController.create(report);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdReport, response.getBody());
    }

    @Test
    void readById_ExistingId_ShouldReturnReport() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long existingId = 123;

        Report report = new Report();
        when(reportService.getById(existingId)).thenReturn(report);

        ResponseEntity<Report> response = reportController.readById(existingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(report, response.getBody());
    }

    @Test
    void readById_NonExistingId_ShouldReturnNotFound() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long nonExistingId = 456;

        when(reportService.getById(nonExistingId)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<Report> response = reportController.readById(nonExistingId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void readByChatId_ExistingChatId_ShouldReturnReports() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long existingChatId = 123;

        List<Report> reports = Arrays.asList(new Report(), new Report());
        when(reportService.getByChatId(existingChatId)).thenReturn(reports);

        ResponseEntity<List<Report>> response = reportController.readByChatId(existingChatId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reports, response.getBody());
    }

    @Test
    void readByChatId_NonExistingChatId_ShouldReturnNotFound() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long nonExistingChatId = 456;

        when(reportService.getByChatId(nonExistingChatId)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<List<Report>> response = reportController.readByChatId(nonExistingChatId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void readLastByChatId_ExistingChatId_ShouldReturnReport() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long existingChatId = 123;

        Report report = new Report();
        when(reportService.getLastReportByChatId(existingChatId)).thenReturn(report);

        ResponseEntity<Report> response = reportController.readLastByChatId(existingChatId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(report, response.getBody());
    }

    @Test
    void readLastByChatId_NonExistingChatId_ShouldReturnNotFound() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long nonExistingChatId = 456;

        when(reportService.getLastReportByChatId(nonExistingChatId)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<Report> response = reportController.readLastByChatId(nonExistingChatId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void readAll_ShouldReturnAllReports() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        List<Report> reports = Arrays.asList(new Report(), new Report());
        when(reportService.getAll()).thenReturn(reports);

        ResponseEntity<List<Report>> response = reportController.readAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reports, response.getBody());
    }

    @Test
    void updatePhotoOnLastReport_ExistingChatId_ShouldReturnUpdatedReport() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long existingChatId = 123;
        String newPhoto = "new-photo.jpg";

        Report updatedReport = new Report();
        when(reportService.updatePhotoOnLastReport(existingChatId, newPhoto)).thenReturn(updatedReport);

        ResponseEntity<Report> response = reportController.updatePhotoOnLastReport(existingChatId, newPhoto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedReport, response.getBody());
    }

    @Test
    void updatePhotoOnLastReport_NonExistingChatId_ShouldReturnNotFound() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long nonExistingChatId = 456;
        String newPhoto = "new-photo.jpg";

        when(reportService.updatePhotoOnLastReport(nonExistingChatId, newPhoto)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<Report> response = reportController.updatePhotoOnLastReport(nonExistingChatId, newPhoto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateTextOnLastReport_ExistingChatId_ShouldReturnUpdatedReport() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long existingChatId = 123;
        String newText = "New text";

        Report updatedReport = new Report();
        when(reportService.updateTextOnLastReport(existingChatId, newText)).thenReturn(updatedReport);

        ResponseEntity<Report> response = reportController.updateTextOnLastReport(existingChatId, newText);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedReport, response.getBody());
    }

    @Test
    void updateTextOnLastReport_NonExistingChatId_ShouldReturnNotFound() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long nonExistingChatId = 456;
        String newText = "New text";

        when(reportService.updateTextOnLastReport(nonExistingChatId, newText)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<Report> response = reportController.updateTextOnLastReport(nonExistingChatId, newText);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateApprovementOnLastReport_ExistingChatId_ShouldReturnUpdatedReport() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long existingChatId = 123;

        Report updatedReport = new Report();
        when(reportService.approveLastReport(existingChatId)).thenReturn(updatedReport);

        ResponseEntity<Report> response = reportController.updateApprovementOnLastReport(existingChatId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedReport, response.getBody());
    }

    @Test
    void updateApprovementOnLastReport_NonExistingChatId_ShouldReturnNotFound() throws EntryNotFoundException {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long nonExistingChatId = 456;

        when(reportService.approveLastReport(nonExistingChatId)).thenThrow(EntryNotFoundException.class);

        ResponseEntity<Report> response = reportController.updateApprovementOnLastReport(nonExistingChatId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void delete_ExistingId_ShouldReturnDeletedReport() {
        ReportService reportService = mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);

        long existingId = 123;

        ResponseEntity<Report> response = reportController.delete(existingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }
}
