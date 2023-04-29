package com.skypro.shelterbot.service;

import com.skypro.shelterbot.model.Info;
import com.skypro.shelterbot.repositories.InfoRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class InfoService {

    private final InfoRepository infoRepository;

    public InfoService(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }


    public Info editInfo(Info info){
        if(infoRepository.findInfoByName(info.getName()) == null){
            throw new RuntimeException();
        }
        return info;
    }


    public Collection<Info> getAllInfo(){
        return infoRepository.findAll();
    }
}