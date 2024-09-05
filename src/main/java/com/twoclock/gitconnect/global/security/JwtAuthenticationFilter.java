package com.twoclock.gitconnect.global.security;

import com.twoclock.gitconnect.global.jwt.service.JwtRedisService;
import com.twoclock.gitconnect.global.jwt.service.JwtService;
import com.twoclock.gitconnect.global.util.CustomResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.twoclock.gitconnect.global.exception.constants.ErrorCode.JWT_BLACKLIST;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtService jwtService;
    private final JwtRedisService jwtRedisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        SecurityContext securityContext = SecurityContextHolder.getContext();

        if (!ObjectUtils.isEmpty(authorization)
                && authorization.startsWith(JwtService.BEARER_PREFIX)
                && securityContext.getAuthentication() == null
        ) {
            String jwtAccessToken = authorization.substring(JwtService.BEARER_PREFIX.length());
            if (!jwtService.validateToken(jwtAccessToken)) {
                filterChain.doFilter(request, response);
                return;
            }
            if (jwtRedisService.isBlacklisted(jwtAccessToken)) {
                CustomResponseUtil.fail(
                        response, JWT_BLACKLIST.getHttpStatus(), JWT_BLACKLIST.getCode(), JWT_BLACKLIST.getMessage()
                );
                return;
            }

            String login = jwtService.getGitHubId(jwtAccessToken);
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(login);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);
        }
        filterChain.doFilter(request, response);
    }
}
