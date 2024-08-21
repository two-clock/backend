package com.twoclock.gitconnect.domain.chat.web;

import com.twoclock.gitconnect.domain.chat.service.ChatRoomService;
import com.twoclock.gitconnect.global.model.RestResponse;
import com.twoclock.gitconnect.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/chatrooms")
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

    @DeleteMapping("/{id}")
    public RestResponse deleteChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("id") Long id
    ) {
        String githubId = userDetails.getUsername();
        chatRoomService.deleteChatRoom(githubId, id);
        return RestResponse.OK();
    }
}
