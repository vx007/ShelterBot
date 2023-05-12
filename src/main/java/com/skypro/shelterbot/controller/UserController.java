package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.service.UserService;
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

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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
                            responseCode = "400",
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
    @PostMapping()
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User addUser = userService.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(addUser);
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
                            responseCode = "400",
                            description = "Если пользователей нет"
                    )
            }
    )
    @GetMapping("by_petId/{petId}")
    public ResponseEntity<User> findUserByPetId(@RequestParam Long petId){
        User user = userService.getByPetId(petId);
        return ResponseEntity.ok(user);
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
                            responseCode = "400",
                            description = "Если пользователей нет"
                    )
            }
    )
    @GetMapping("by_chatId/{chatId}")
    public ResponseEntity<User> findUserByChatId(@PathVariable Long chatId){
        User user = userService.getByChatId(chatId);
        return ResponseEntity.ok(user);
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
                            responseCode = "400",
                            description = "Если пользователей нет"
                    )
            }
    )
    @GetMapping("all_users")
    public ResponseEntity<Collection<User>> getALlUsers() {
        Collection<User> users = userService.getAll();
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.getAll());
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
                            responseCode = "400",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("update_name/{chatId}")
    public ResponseEntity<User> updateName(@PathVariable("chatId") Long chatId, @RequestBody String name) {
        userService.updateName(chatId, name);
        return ResponseEntity.ok().build();
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
                            responseCode = "400",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("update_phone/{chatId}")
    public ResponseEntity<User> updatePhone(@PathVariable("chatId") Long chatId, @RequestBody String phone) {
        userService.updatePhone(chatId, phone);
        return ResponseEntity.ok().build();
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
                            responseCode = "400",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("update_last_command/{chatId}")
    public ResponseEntity<User> updateLastCommand(@PathVariable("chatId") Long chatId, @RequestBody String command) {
        userService.updateLastCommand(chatId, command);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Удалить пользователя по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "пользователь удален",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если пользователя с таким id нет"
                    )
            }
    )
    @DeleteMapping("{chatId}")
    public void removeUserById(@PathVariable Long chatId) {
        userService.remove(chatId);
    }
}
