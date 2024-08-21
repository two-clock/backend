package com.twoclock.gitconnect.domain.chat.web;

import com.twoclock.gitconnect.domain.chat.service.ChatRoomService;
import com.twoclock.gitconnect.global.model.RestResponse;
import com.twoclock.gitconnect.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public RestResponse createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam("receiveId") String receiveGitHubId
    ) {
        String createdGitHubId = userDetails.getUsername();
        chatRoomService.createChatRoom(createdGitHubId, receiveGitHubId);
        return RestResponse.OK();
    }
}
