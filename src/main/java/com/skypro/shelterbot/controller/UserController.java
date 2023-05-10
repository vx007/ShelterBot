package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.model.User;
import com.skypro.shelterbot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
            summary = "Вывести список заявок по полученному значению",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список заявок выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = User.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если заявок нет"
                    )
            }
    )
    @GetMapping("by_petId")
    public ResponseEntity<User> findUserByPetId(@RequestParam Long id){
        User user = userService.getByPetId(id);
        return ResponseEntity.ok(userService.getByPetId(id));
    }
}
