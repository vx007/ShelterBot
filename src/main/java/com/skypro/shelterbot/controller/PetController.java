package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.exception.EntryNotFoundException;
import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.model.PetType;
import com.skypro.shelterbot.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("pets")
public class PetController {
    private final PetService petService;

    @Operation(
            summary = "Добавление питомца в приют",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "питомец добавлен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Pet.class)
                                    )
                            }
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "новый питомец",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = Pet.class)
                            )
                    }
            )
    )
    @PostMapping("create")
    public ResponseEntity<Pet> create(@Parameter(name = "питомец") @RequestBody Pet pet) {
        var newPet = petService.add(pet);
        return ResponseEntity.ok(newPet);
    }

    @Operation(
            summary = "Поиск питомца по базе данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "питомец найден",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Pet.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если питомца нет"
                    )
            }
    )
    @GetMapping("by_id/{id}")
    public ResponseEntity<Pet> readById(@PathVariable Long id) {
        try {
            var pet = petService.getById(id);
            return ResponseEntity.ok(pet);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Вывести список питомцев",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "список питомцев выведен",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Pet.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если питомцев нет"
                    )
            }
    )
    @GetMapping("all")
    public ResponseEntity<List<Pet>> readAll() {
        try {
            var pets = petService.getAll();
            return ResponseEntity.ok(pets);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить тип",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "тип изменён",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Pet.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("update_type/{id}")
    public ResponseEntity<Pet> updateType(@PathVariable Long id, @RequestBody PetType type) {
        try {
            var pet = petService.updateType(id, type);
            return ResponseEntity.ok(pet);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить имя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "имя изменено",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Pet.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("update_name/{id}")
    public ResponseEntity<Pet> updateName(@PathVariable Long id, @RequestBody String name) {
        try {
            var pet = petService.updateName(id, name);
            return ResponseEntity.ok(pet);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить возраст",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "возраст изменён",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Pet.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("update_age/{id}")
    public ResponseEntity<Pet> updateAge(@PathVariable Long id, @RequestBody Integer age) {
        try {
            var pet = petService.updateAge(id, age);
            return ResponseEntity.ok(pet);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Изменить породу",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "порода изменена",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Pet.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователей нет"
                    )
            }
    )
    @PutMapping("update_breed/{id}")
    public ResponseEntity<Pet> updateBreed(@PathVariable Long id, @RequestBody String breed) {
        try {
            var pet = petService.updateBreed(id, breed);
            return ResponseEntity.ok(pet);
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Удалить питомца по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "питомец удален",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Pet.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если питомца с таким id нет"
                    )
            }
    )
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Pet> removePetById(@PathVariable Long id) {
        try {
            petService.remove(id);
            return ResponseEntity.ok().build();
        } catch (EntryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
