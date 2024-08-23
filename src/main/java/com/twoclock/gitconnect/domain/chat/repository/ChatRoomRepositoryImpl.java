package com.twoclock.gitconnect.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twoclock.gitconnect.domain.chat.entity.ChatRoom;
import com.twoclock.gitconnect.domain.chat.entity.QChatRoom;
import com.twoclock.gitconnect.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatRoomRepositoryImpl implements CustomChatRoomRepository {

    private final JPAQueryFactory queryFactory;

    public ChatRoomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ChatRoom> findUniqueChatRoomsByMember(Member member) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        return queryFactory.selectFrom(chatRoom)
                .where(chatRoom.createdMember.eq(member)
                        .or(chatRoom.receivedMember.eq(member))
                )
                .groupBy(chatRoom.id)
                .fetch();
    }
}
