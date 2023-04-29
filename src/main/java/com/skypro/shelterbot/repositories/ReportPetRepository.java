package com.skypro.shelterbot.repositories;

import com.skypro.shelterbot.model.ReportPet;
import com.skypro.shelterbot.model.ShelterCatUser;
import com.skypro.shelterbot.model.ShelterDogUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

public interface ReportPetRepository extends JpaRepository<ReportPet,Long> {

    ReportPet findReportPetById(long id);

    Collection<ReportPet> findReportPetByDateTimeBetween(LocalDateTime begin, LocalDateTime end);

    Collection<ReportPet> findReportPetByShelterCatUser(ShelterCatUser shelterCatUser);

    Collection<ReportPet> findReportPetByShelterDogUser(ShelterDogUser shelterDogUser);
}
