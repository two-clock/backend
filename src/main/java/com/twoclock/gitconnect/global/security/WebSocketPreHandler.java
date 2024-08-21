package com.twoclock.gitconnect.global.security;

import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.jwt.service.JwtRedisService;
import com.twoclock.gitconnect.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Objects;

import static com.twoclock.gitconnect.global.exception.constants.ErrorCode.JWT_BLACKLIST;
import static com.twoclock.gitconnect.global.exception.constants.ErrorCode.JWT_EXPIRED;
import static com.twoclock.gitconnect.global.jwt.service.JwtService.BEARER_PREFIX;

@RequiredArgsConstructor
@Configuration
public class WebSocketPreHandler implements ChannelInterceptor {

    private final JwtService jwtService;
    private final JwtRedisService jwtRedisService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand command = Objects.requireNonNull(accessor).getCommand();

        if (isCommandToSkip(command)) {
            return message;
        }

        if (Objects.equals(command, StompCommand.ERROR)) {
            throw new MessageDeliveryException("Error during WebSocket communication");
        }

        String authorizationHeader = getAuthorizationHeader(accessor);
        if (isValidToken(authorizationHeader)) {
            String jwtAccessToken = authorizationHeader.substring(BEARER_PREFIX.length());
            validateToken(jwtAccessToken);

            String login = jwtService.getGitHubId(jwtAccessToken);
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(login);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            accessor.setUser(authenticationToken);
        }
        return message;
    }

    private boolean isCommandToSkip(StompCommand command) {
        return command == StompCommand.UNSUBSCRIBE || command == StompCommand.MESSAGE ||
                command == StompCommand.CONNECTED || command == StompCommand.SEND;
    }

    private String getAuthorizationHeader(StompHeaderAccessor accessor) {
        List<String> authorizationHeader = accessor.getNativeHeader(HttpHeaders.AUTHORIZATION);
        return (authorizationHeader != null && !authorizationHeader.isEmpty()) ? authorizationHeader.get(0) : "";
    }

    private boolean isValidToken(String authorizationHeader) {
        return authorizationHeader.startsWith(BEARER_PREFIX);
    }

    private void validateToken(String jwtAccessToken) {
        if (!jwtService.validateToken(jwtAccessToken)) {
            throw new CustomException(JWT_EXPIRED);
        }
        if (jwtRedisService.isBlacklisted(jwtAccessToken)) {
            throw new CustomException(JWT_BLACKLIST);
        }
    }
}
