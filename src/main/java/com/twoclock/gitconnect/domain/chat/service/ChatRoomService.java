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

import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createChatRoom(String createdGitHubId, String receiveGitHubId) {
        Member createdMember = validateMemberByGitHubId(createdGitHubId);
        Member receiveMember = validateMemberByGitHubId(receiveGitHubId);

        String chatRoomId = getChatRoomId(createdMember.getGitHubId(), receiveMember.getGitHubId());

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .createdMember(createdMember)
                .receivedMember(receiveMember)
                .build();

        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void deleteChatRoom(String githubId, Long id) {
        Member member = validateMemberByGitHubId(githubId);
        ChatRoom chatRoom = validateChatRoomById(id);

        checkAccessChatRoom(member.getGitHubId(), chatRoom.getChatRoomId());
        chatRoomRepository.delete(chatRoom);
    }

    private ChatRoom validateChatRoomById(Long id) {
        return chatRoomRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM)
        );
    }

    private Member validateMemberByGitHubId(String gitHubId) {
        return memberRepository.findByGitHubId(gitHubId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }

    private String getChatRoomId(String createdGitHubId, String receiveGitHubId) {
        long createdGitHubIdAsLong = Long.parseLong(createdGitHubId);
        long receiveGitHubIdAsLong = Long.parseLong(receiveGitHubId);

        long minMemberId = Math.min(createdGitHubIdAsLong, receiveGitHubIdAsLong);
        long maxMemberId = Math.max(createdGitHubIdAsLong, receiveGitHubIdAsLong);
        String chatRoomId = minMemberId + ":" + maxMemberId;

        if (chatRoomRepository.existsByChatRoomId(chatRoomId)) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_CHAT_ROOM);
        }
        return chatRoomId;
    }

    private void checkAccessChatRoom(String githubId, String chatRoomId) {
        boolean isCheck = Arrays.asList(chatRoomId.split(":")).contains(githubId);
        if (!isCheck) {
            throw new CustomException(ErrorCode.NO_ACCESS_CHAT_ROOM);
        }
    }
}
