package com.twoclock.gitconnect.domain.chat.repository;

import com.twoclock.gitconnect.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByChatRoomId(String chatRoomId);

    Optional<ChatRoom> findByChatRoomId(String chatRoomId);
}
