package com.skypro.shelterbot.repositories;

import com.skypro.shelterbot.model.Info;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface InfoRepository extends JpaRepository<Info,Long> {

    Info findInfoByName(String name);

}
