package com.skypro.shelterbot.service;

import com.skypro.shelterbot.entity.Chat;
import com.skypro.shelterbot.repo.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Transactional
    public void add(Chat chat) {
        chatRepository.save(chat);
    }

    public Optional<Chat> find(Long id) {
        return chatRepository.findById(id);
    }

    @Transactional
    public void remove(Chat chat) {
        chatRepository.delete(chat);
    }
}
