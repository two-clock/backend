package com.twoclock.gitconnect.domain.chat.repository;

import com.twoclock.gitconnect.domain.chat.entity.ChatRoom;
import com.twoclock.gitconnect.domain.member.entity.Member;

import java.util.List;

public interface CustomChatRoomRepository {

    List<ChatRoom> findUniqueChatRoomsByMember(Member member);
}
