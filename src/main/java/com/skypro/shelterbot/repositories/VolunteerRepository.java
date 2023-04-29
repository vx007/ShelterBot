package com.skypro.shelterbot.repositories;

import com.skypro.shelterbot.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer,Long> {
}
