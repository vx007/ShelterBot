package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.exception.EntryAlreadyExists;
import com.skypro.shelterbot.exception.EntryNotFoundException;
import com.skypro.shelterbot.model.TrialPeriod;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Добавление пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "пользователь добавлен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "406",
                            description = "Если пользователь уже добавлен"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "новый пользователь",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = User.class)
                            )
                    }
            )
    )
    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody User user) {
        try {
            var newUser = userService.add(user);
            return ResponseEntity.ok(newUser);
        } catch (EntryAlreadyExists e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }

    @Operation(
            summary = "Вывести список пользователей по chatId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список пользователей выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @GetMapping("/by_chat-id/{chatId}")
    public ResponseEntity<User> readByChatId(@PathVariable Long chatId) {
        try {
            var user = userService.getByChatId(chatId);
            return ResponseEntity.ok(user);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Вывести список пользователей, прикрепленных к животному по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список пользователей выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @GetMapping("/by_pet-id/{petId}")
    public ResponseEntity<User> readByPetId(@PathVariable Long petId) {
        try {
            var user = userService.getByPetId(petId);
            return ResponseEntity.ok(user);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Вывести список пользователей",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список пользователей выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<User>> readAll() {
        try {
            var users = userService.getAll();
            return ResponseEntity.ok(users);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить имя пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "имя пользователя изменено",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("/update_name/{chatId}")
    public ResponseEntity<User> updateName(@PathVariable Long chatId, @RequestBody String name) {
        try {
            var user = userService.updateName(chatId, name);
            return ResponseEntity.ok(user);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить телефон пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "телефон пользователя изменен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("/update_phone/{chatId}")
    public ResponseEntity<User> updatePhone(@PathVariable Long chatId, @RequestBody String phone) {
        try {
            var user = userService.updatePhone(chatId, phone);
            return ResponseEntity.ok(user);
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
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("/update_last_command/{chatId}")
    public ResponseEntity<User> updateLastCommand(@PathVariable Long chatId, @RequestBody String command) {
        try {
            var user = userService.updateLastCommand(chatId, command);
            return ResponseEntity.ok(user);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить испытательный срок пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Испытательный срок пользователя изменен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("/update_period/{chatId}")
    public ResponseEntity<User> updatePeriod(@PathVariable Long chatId, @RequestBody TrialPeriod period) {
        try {
            var user = userService.updatePeriod(chatId, period);
            return ResponseEntity.ok(user);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить статус \"Нужна помощь волонтера\"",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статус пользователя изменен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("/update_volunteer_status/{chatId}")
    public ResponseEntity<User> updateVolunteerStatus(@PathVariable Long chatId) {
        try {
            var user = userService.updateVolunteerStatus(chatId);
            return ResponseEntity.ok(user);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить животное у пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Животное пользователя изменено",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь или животное не найдено"
                    )
            }
    )
    @PutMapping("/update_pet/{chatId}")
    public ResponseEntity<User> updatePet(@PathVariable Long chatId, @RequestBody Long petId) {
        try {
            var user = userService.updatePet(chatId, petId);
            return ResponseEntity.ok(user);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Удалить пользователя по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь удален",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователя с таким id нет"
                    )
            }
    )
    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<User> delete(@PathVariable Long chatId) {
        try {
            userService.remove(chatId);
            return ResponseEntity.ok().build();
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
