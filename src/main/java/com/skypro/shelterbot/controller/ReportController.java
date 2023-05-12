package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("report")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(
            summary = "Вывести список всех отчетов",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список отчетов выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Report.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если отчетов нет"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный параметр"
                    )
            }
    )
    @GetMapping("all_reports")
    public ResponseEntity<Collection<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAll());
    }

    @Operation(
            summary = "Вывести список отчетов по айди владельца питомца",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список отчетов выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Report.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный аргумент"
                    )
            }
    )
    @GetMapping("/user/{id}")
    public ResponseEntity<Optional<Report>> getAllReportByUserId(@PathVariable long id) {
        Optional<Report> userReports;
        try {
            userReports = Optional.ofNullable(reportService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();

        }
        return ResponseEntity.ok(userReports);
    }
}
