package com.twoclock.gitconnect.domain.chat.service;

import com.twoclock.gitconnect.domain.chat.dto.ChatMessageSaveReqDto;
import com.twoclock.gitconnect.domain.chat.dto.ChatMessageSaveRespDto;
import com.twoclock.gitconnect.domain.chat.entity.ChatMessage;
import com.twoclock.gitconnect.domain.chat.entity.ChatRoom;
import com.twoclock.gitconnect.domain.chat.repository.ChatMessageRepository;
import com.twoclock.gitconnect.domain.chat.repository.ChatRoomRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatMessageSaveRespDto sendMessage(String gitHubId, String chatRoomId, ChatMessageSaveReqDto chatMessageSaveReqDto) {
        Member member = validateMemberByGitHubId(gitHubId);
        ChatRoom chatRoom = validateChatRoomById(chatRoomId);

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .senderGitHubId(member.getGitHubId())
                .message(chatMessageSaveReqDto.message())
                .createdDateTime(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatMessage);

        return new ChatMessageSaveRespDto(
                member.getLogin(), member.getGitHubId(), chatMessage.getMessage(), chatMessage.getCreatedDateTime()
        );
    }

    private Member validateMemberByGitHubId(String gitHubId) {
        return memberRepository.findByGitHubId(gitHubId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }

    private ChatRoom validateChatRoomById(String chatRoomId) {
        return chatRoomRepository.findByChatRoomId(chatRoomId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
        );
    }
}