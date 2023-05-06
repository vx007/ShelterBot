package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.enums.TrialPeriod;
import com.skypro.shelterbot.exception.OwnerNotFoundException;
import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.ShelterCatUser;
import com.skypro.shelterbot.model.ShelterDogUser;
import com.skypro.shelterbot.service.ShelterUserService;
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
import java.util.List;

@RestController
@RequestMapping("shelter_user")
public class ShelterUserController {
    private final ShelterUserService shelterUserService;

    public ShelterUserController(ShelterUserService shelterUserService) {
        this.shelterUserService = shelterUserService;
    }

    @Operation(
            summary = "Добавление владельца кошек в БД",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "владелец кошек добавлен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Pet.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если владелец кошек уже добавлен"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "новый владелец кошек",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ShelterCatUser.class)
                            )
                    }
            )
    )
    @PostMapping("shelter_cat_user")
    public ResponseEntity<ShelterCatUser> addCatUser(@RequestBody ShelterCatUser shelterCatUser){
        ShelterCatUser addCatUser = shelterUserService.addCatUser(shelterCatUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(addCatUser);
    }

    @Operation(
            summary = "Добавление владельца собак в БД",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "владелец собак добавлен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Pet.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если владелец собак уже добавлен"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "новый владелец собак",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ShelterDogUser.class)
                            )
                    }
            )
    )
    @PostMapping("shelter_dog_user")
    public ResponseEntity<ShelterDogUser> addDogUser(@RequestBody ShelterDogUser shelterDogUser){
        ShelterDogUser addDogUser = shelterUserService.addDogUser(shelterDogUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(addDogUser);
    }

    @Operation(
            summary = "Вывести список всех владельцев кошек",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список владельцев кошек выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ShelterCatUser.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если владельцев кошек нет"
                    )
            }
    )
    @GetMapping("all_cat_user")
    public ResponseEntity<Collection<ShelterCatUser>> getAllCatUser(){
        return ResponseEntity.ok(shelterUserService.allCatUser());
    }

    @Operation(
            summary = "Вывести список всех владельцев собак",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список владельцев собак выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ShelterDogUser.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если владельцев собак нет"
                    )
            }
    )
    @GetMapping("all_dog_user")
    public ResponseEntity<Collection<ShelterDogUser>> getAllDogUser(){
        return ResponseEntity.ok(shelterUserService.allDogUser());
    }


    @Operation(
            summary = "Вывести список владельцев собак с завершившимся испытательным периодом",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список владельцев собак выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ShelterDogUser.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если владельцев собак нет"
                    )
            }
    )
    @GetMapping("dog/end_trial_period")
    public ResponseEntity<List<ShelterDogUser>> getDogUserEndTrialPeriod() {
        List<ShelterDogUser> dogUsers = shelterUserService.getDogUserEndTrialPeriod();
        if (dogUsers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dogUsers);
    }

    @Operation(
            summary = "Вывести список владельцев кошек с завершенным тестовым периодом",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список владельцев кошек выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ShelterCatUser.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если владельцев кошек нет"
                    )
            }
    )
    @GetMapping("cat/end_trial_period")
    public ResponseEntity<List<ShelterCatUser>> getCatOwnersEndTrialPeriod() {
        List<ShelterCatUser> catUsers = shelterUserService.getCatUserEndTrialPeriod();
        if (catUsers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(catUsers);
    }

    @Operation(
            summary = "Изменить статус и дату окончания испытательного периода для владельца кошки",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Обновленный владелец кошки",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ShelterCatUser.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если по указанному id владелец кошки не найден"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если в запросе указан некорректный параметр"
                    )
            }
    )
    @PatchMapping("/cat/{userId}")
    public ResponseEntity<ShelterCatUser> changeStatusTrialPeriodCat(@PathVariable @Parameter(description = "Иденификатор владельца") long userId,
                                                               @RequestParam(name = "new_STP") @Parameter(description = "Новый статус для испытательного периода") TrialPeriod stp) {
        ShelterCatUser shelterCatUser;
        try {
            shelterCatUser = shelterUserService.changeStatusTrialPeriodCat(userId, stp);
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(shelterCatUser);
    }

    @Operation(
            summary = "Изменить статус и дату окончания испытательного периода для владельца собаки",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Обновленный владелец собаки",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ShelterDogUser.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если по указанному id владелец собаки не найден"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Если в запросе указан некорректный параметр"
                    )
            }
    )
    @PatchMapping("/dog/{userId}")
    public ResponseEntity<ShelterDogUser> changeStatusTrialPeriodDog(@PathVariable @Parameter(description = "Иденификатор владельца") Long userId,
                                                               @RequestParam(name = "new_STP") @Parameter(description = "Новый статус для испытательного периода") TrialPeriod stp) {
        ShelterDogUser shelterDogUser;
        try {
            shelterDogUser = shelterUserService.changeStatusTrialPeriodDog(userId, stp);
        } catch (OwnerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(shelterDogUser);
    }
}
