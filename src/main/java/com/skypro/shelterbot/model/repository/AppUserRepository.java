package com.skypro.shelterbot.model.repository;

import com.skypro.shelterbot.model.AppUser;
import org.springframework.data.repository.CrudRepository;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {
}
