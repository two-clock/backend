package com.twoclock.gitconnect.domain.chat.web;

import com.twoclock.gitconnect.domain.chat.dto.ChatMessageSaveReqDto;
import com.twoclock.gitconnect.domain.chat.dto.ChatMessageSaveRespDto;
import com.twoclock.gitconnect.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/{chatRoomId}")
    @SendTo("/sub/{chatRoomId}")
    public ChatMessageSaveRespDto sendMessage(
            Principal userDetails,
            @DestinationVariable(value = "chatRoomId") String chatRoomId,
            @Payload ChatMessageSaveReqDto chatMessageSaveReqDto
    ) {
        String gitHubId = userDetails.getName();
        return chatMessageService.sendMessage(gitHubId, chatRoomId, chatMessageSaveReqDto);
    }
}
