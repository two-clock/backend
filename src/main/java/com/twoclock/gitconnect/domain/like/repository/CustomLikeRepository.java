package com.twoclock.gitconnect.domain.like.repository;

import com.twoclock.gitconnect.domain.like.dto.LikePopularWeekMemberRespDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomLikeRepository {

    List<LikePopularWeekMemberRespDto> findTopMemberByLikesBetween(
            LocalDateTime startDateTime, LocalDateTime endDateTime, int limit
    );
}
