package com.skypro.shelterbot.repository;

import com.skypro.shelterbot.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserChatId(Long chatId);
}
