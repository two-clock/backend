package com.twoclock.gitconnect.domain.chat.service;

import com.twoclock.gitconnect.domain.chat.dto.ChatMessageRespDto;
import com.twoclock.gitconnect.domain.chat.entity.ChatRoom;
import com.twoclock.gitconnect.domain.chat.repository.ChatMessageRepository;
import com.twoclock.gitconnect.domain.chat.repository.ChatRoomRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private static final String CHATROOM_DELIMITER = "-";

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public void createChatRoom(String createdGitHubId, String receiveGitHubId) {
        Member createdMember = validateMemberByGitHubId(createdGitHubId);
        Member receiveMember = validateMemberByGitHubId(receiveGitHubId);

        String chatRoomId = generateChatRoomId(createdMember.getGitHubId(), receiveMember.getGitHubId());

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .createdMember(createdMember)
                .receivedMember(receiveMember)
                .build();

        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void deleteChatRoom(String githubId, String chatRoomId) {
        Member member = validateMemberByGitHubId(githubId);
        ChatRoom chatRoom = validateChatRoomById(chatRoomId);

        checkAccessChatRoom(member.getGitHubId(), chatRoom.getChatRoomId());
        chatRoomRepository.delete(chatRoom);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageRespDto> chatRoomMessages(String githubId, String chatRoomId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "createdDateTime");

        Member member = validateMemberByGitHubId(githubId);
        ChatRoom chatRoom = validateChatRoomById(chatRoomId);
        checkAccessChatRoom(member.getGitHubId(), chatRoom.getChatRoomId());

        return chatMessageRepository.findAllByChatRoomId(chatRoomId, pageRequest)
                .getContent()
                .stream()
                .map(chatRoomMessage -> new ChatMessageRespDto(
                        chatRoomMessage.getSenderMember().getLogin(),
                        chatRoomMessage.getMessage(),
                        chatRoomMessage.getCreatedDateTime()
                ))
                .toList();
    }

    private ChatRoom validateChatRoomById(String chatRoomId) {
        return chatRoomRepository.findByChatRoomId(chatRoomId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
        );
    }

    private Member validateMemberByGitHubId(String gitHubId) {
        return memberRepository.findByGitHubId(gitHubId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }

    private String generateChatRoomId(String createdGitHubId, String receiveGitHubId) {
        long createdGitHubIdAsLong = Long.parseLong(createdGitHubId);
        long receiveGitHubIdAsLong = Long.parseLong(receiveGitHubId);

        long minMemberId = Math.min(createdGitHubIdAsLong, receiveGitHubIdAsLong);
        long maxMemberId = Math.max(createdGitHubIdAsLong, receiveGitHubIdAsLong);
        String chatRoomId = minMemberId + CHATROOM_DELIMITER + maxMemberId;

        if (chatRoomRepository.existsByChatRoomId(chatRoomId)) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_CHAT_ROOM);
        }
        return chatRoomId;
    }

    private void checkAccessChatRoom(String githubId, String chatRoomId) {
        boolean isCheck = Arrays.asList(chatRoomId.split(CHATROOM_DELIMITER)).contains(githubId);
        if (!isCheck) {
            throw new CustomException(ErrorCode.NO_ACCESS_CHAT_ROOM);
        }
    }
}
