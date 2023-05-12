package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
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
            summary = "Добавление отчета",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "отчет добавлен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Report.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если отчет уже есть"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "новый отчет",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = Report.class)
                            )
                    }
            )
    )
    @PostMapping()
    public ResponseEntity<Report> addReport(@RequestBody Report report) {
        Report addReport = reportService.add(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(addReport);
    }

    @Operation(
            summary = "Поиск отчета по базе данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "отчет найден",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Report.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если отчета нет"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "имеющийся отчет",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = Report.class)
                            )
                    }
            )
    )
    @GetMapping("{id}")
    public ResponseEntity<Report> findReport(@PathVariable Long id) {
        Report report = reportService.getById(id);
        return ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Вывести список всех отчетов по chat_id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список отчетов по chat_id выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Report.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если отчетов по chat_id нет"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный параметр"
                    )
            }
    )
    @GetMapping("reports_by_chatId/{chatId}")
    public ResponseEntity<Collection<Report>> getAllReports(@PathVariable Long chatId) {
        return ResponseEntity.ok(reportService.getByChatId(chatId));
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
    public ResponseEntity<Report> getAllReportByUserId(@PathVariable long id) {
        Report userReports;
        try {
            userReports = reportService.getById(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();

        }
        return ResponseEntity.ok(userReports);
    }

    @Operation(
            summary = "Удалить отчет по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "отчет удален",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Report.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если отчета с таким id нет"
                    )
            }
    )
    @DeleteMapping("{id}")
    public void removeReportById(@PathVariable Long id) {
        reportService.remove(id);
    }
}
