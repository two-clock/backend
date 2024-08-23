package com.twoclock.gitconnect.domain.chat.repository;

import com.twoclock.gitconnect.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Page<ChatMessage> findAllByChatRoomId(String chatId, Pageable pageable);
}
