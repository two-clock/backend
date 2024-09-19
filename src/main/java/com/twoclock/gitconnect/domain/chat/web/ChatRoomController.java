package com.twoclock.gitconnect.domain.chat.web;

import com.twoclock.gitconnect.domain.chat.dto.ChatMessageRespDto;
import com.twoclock.gitconnect.domain.chat.dto.ChatRoomRespDto;
import com.twoclock.gitconnect.domain.chat.service.ChatRoomService;
import com.twoclock.gitconnect.global.model.RestResponse;
import com.twoclock.gitconnect.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/chatrooms")
@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public RestResponse createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("receiveLogin") String receiveLogin
    ) {
        String gitHubId = userDetails.getUsername();
        chatRoomService.createChatRoom(gitHubId, receiveLogin);
        return RestResponse.OK();
    }

    @DeleteMapping("/{chatRoomId}")
    public RestResponse deleteChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("chatRoomId") String chatRoomId
    ) {
        String githubId = userDetails.getUsername();
        chatRoomService.deleteChatRoom(githubId, chatRoomId);
        return RestResponse.OK();
    }

    @GetMapping
    public RestResponse getChatRooms(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String githubId = userDetails.getUsername();
        List<ChatRoomRespDto> responseDto = chatRoomService.getChatRooms(githubId);
        return new RestResponse(responseDto);
    }

    @GetMapping("/{chatRoomId}/messages")
    public RestResponse chatRoomMessages(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("chatRoomId") String chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        String githubId = userDetails.getUsername();
        List<ChatMessageRespDto> responseDto = chatRoomService.chatRoomMessages(githubId, chatRoomId, page, size);
        return new RestResponse(responseDto);
    }
}
