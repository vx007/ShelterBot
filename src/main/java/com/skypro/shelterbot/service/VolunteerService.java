package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Volunteer;
import com.skypro.shelterbot.repositories.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class VolunteerService {
    private VolunteerRepository volunteerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    public Volunteer addVolunteer(Volunteer volunteer){
        return volunteerRepository.save(volunteer);
    }

    public Collection<Volunteer> getAllVolunteer(){
        return volunteerRepository.findAll();
    }
}
