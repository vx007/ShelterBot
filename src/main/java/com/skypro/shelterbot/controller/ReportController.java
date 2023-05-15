package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.exception.EntryNotFoundException;
import com.skypro.shelterbot.model.Report;
import com.skypro.shelterbot.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;

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
    @PostMapping("/create")
    public ResponseEntity<Report> create(@RequestBody Report report) {
        var newReport = reportService.add(report);
        return ResponseEntity.ok(newReport);
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
                            responseCode = "404",
                            description = "Если отчета нет"
                    )
            }
    )
    @GetMapping("/by_id/{id}")
    public ResponseEntity<Report> readById(@PathVariable Long id) {
        try {
            var report = reportService.getById(id);
            return ResponseEntity.ok(report);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
                    )
            }
    )
    @GetMapping("/by_chat-id/{chatId}")
    public ResponseEntity<List<Report>> readByChatId(@PathVariable Long chatId) {
        try {
            var reports = reportService.getByChatId(chatId);
            return ResponseEntity.ok(reports);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Вывести последний отчёт по chat_id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Последний отчёт по chat_id выведен",
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
                    )
            }
    )
    @GetMapping("/last_by_chat-id/{chatId}")
    public ResponseEntity<Report> readLastByChatId(@PathVariable Long chatId) {
        try {
            var report = reportService.getLastReportByChatId(chatId);
            return ResponseEntity.ok(report);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<Report>> readAll() {
        try {
            var reports = reportService.getAll();
            return ResponseEntity.ok(reports);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить фото последнего отчёта пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Фото изменено",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Report.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Последний отчёт не найден"
                    )
            }
    )
    @PutMapping("/update_photo/{chatId}")
    public ResponseEntity<Report> updatePhotoOnLastReport(@PathVariable Long chatId, @RequestBody String photo) {
        try {
            var lastReport = reportService.updatePhotoOnLastReport(chatId, photo);
            return ResponseEntity.ok(lastReport);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить текст последнего отчёта пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Текст изменён",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Report.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Последний отчёт не найден"
                    )
            }
    )
    @PutMapping("/update_text/{chatId}")
    public ResponseEntity<Report> updateTextOnLastReport(@PathVariable Long chatId, @RequestBody String text) {
        try {
            var lastReport = reportService.updateTextOnLastReport(chatId, text);
            return ResponseEntity.ok(lastReport);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить последнюю команду пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "последняя команда пользователя изменена",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Report.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("/update_approvement/{chatId}")
    public ResponseEntity<Report> updateApprovementOnLastReport(@PathVariable Long chatId) {
        try {
            var lastReport = reportService.approveLastReport(chatId);
            return ResponseEntity.ok(lastReport);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
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
                            responseCode = "404",
                            description = "Если отчета с таким id нет"
                    )
            }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Report> delete(@PathVariable Long id) {
        try {
            reportService.remove(id);
            return ResponseEntity.ok().build();
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
