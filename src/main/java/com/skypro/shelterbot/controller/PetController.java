package com.skypro.shelterbot.controller;

import com.skypro.shelterbot.model.Pet;
import com.skypro.shelterbot.service.PetService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping("/pets")
//@Tag (name = "Питомцы", description = "CRUD-операции с питомцами приюта.")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

//    @ApiResponses( {
//            @ApiResponse( responseCode = "200",
//                    description = "Питомец добавлен успешно.",
//                            schema = @Schema(implementation = Pet.class)
//                    )}
//            ),
//            @ApiResponse( responseCode = "500",
//                    description = "Ошибка сервера. Повторите запрос."  )
//    } )

    @PostMapping
    public Pet addPet (@RequestBody Pet pet) {
        return this.petService.addPet(pet);
    }

    @GetMapping
    public Collection<Pet> getAllPets () {
        return petService.getAllPets();
    }

//    @GetMapping
//    public Pet getPetBiId (@RequestParam Long idPet) {
//        return petService.getPetById();
//    }
}
