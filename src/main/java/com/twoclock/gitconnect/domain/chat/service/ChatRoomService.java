package com.twoclock.gitconnect.domain.chat.service;

import com.twoclock.gitconnect.domain.chat.entity.ChatRoom;
import com.twoclock.gitconnect.domain.chat.repository.ChatRoomRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createChatRoom(String createdGitHubId, String receiveGitHubId) {
        Member createdMember = validateMember(createdGitHubId);
        Member receiveMember = validateMember(receiveGitHubId);

        String chatRoomId = validateChatRoom(createdMember, receiveMember);

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .createdMember(createdMember)
                .receivedMember(receiveMember)
                .build();

        chatRoomRepository.save(chatRoom);
    }

    private Member validateMember(String createdGitHubId) {
        return memberRepository.findByGitHubId(createdGitHubId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }

    private String validateChatRoom(Member createdMember, Member receiveMember) {
        long minMemberId = Math.min(createdMember.getId(), receiveMember.getId());
        long maxMemberId = Math.max(createdMember.getId(), receiveMember.getId());

        String chatRoomId = minMemberId + ":" + maxMemberId;

        if (chatRoomRepository.existsByChatRoomId(chatRoomId)) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_CHAT_ROOM);
        }
        return chatRoomId;
    }
}
