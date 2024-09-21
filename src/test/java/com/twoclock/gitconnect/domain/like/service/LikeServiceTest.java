package com.twoclock.gitconnect.domain.like.service;

import com.twoclock.gitconnect.domain.like.dto.LikePopularWeekMemberRespDto;
import com.twoclock.gitconnect.domain.like.repository.LikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @DisplayName("주간 인기 멤버 조회가 정상적으로 작동 하는지 확인")
    @Test
    void getPopularWeekMember_1() {
        // given
        LocalDateTime startDateTime = LocalDateTime.of(2024, 9, 16, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 9, 22, 23, 59, 59);
        int limit = 5;

        // when
        when(likeRepository.findTopMemberByLikesBetween(startDateTime, endDateTime, limit))
                .thenReturn(List.of(
                        new LikePopularWeekMemberRespDto("login-1", "avatar-1"),
                        new LikePopularWeekMemberRespDto("login-2", "avatar-2")
                ));

        List<LikePopularWeekMemberRespDto> result = likeService.getPopularWeekMember();

        // then
        assertEquals(2, result.size());
    }
}