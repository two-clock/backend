package com.twoclock.gitconnect.domain.like.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.like.dto.LikePopularWeekMemberRespDto;
import com.twoclock.gitconnect.domain.like.dto.LikePopularWeekRepositoryRespDto;
import com.twoclock.gitconnect.domain.like.dto.QLikePopularWeekMemberRespDto;
import com.twoclock.gitconnect.domain.like.dto.QLikePopularWeekRepositoryRespDto;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static com.twoclock.gitconnect.domain.board.entity.QBoard.board;
import static com.twoclock.gitconnect.domain.like.entity.QLikes.likes;
import static com.twoclock.gitconnect.domain.member.entity.QMember.member;

public class LikeRepositoryImpl implements CustomLikeRepository {

    private final JPAQueryFactory queryFactory;

    public LikeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<LikePopularWeekMemberRespDto> findTopMemberByLikesBetween(
            LocalDateTime startDateTime, LocalDateTime endDateTime, int limit
    ) {
        return queryFactory.select(new QLikePopularWeekMemberRespDto(member.login, member.avatarUrl))
                .from(likes)
                .join(likes.board, board)
                .join(board.member, member)
                .where(likes.createdDateTime.between(startDateTime, endDateTime)
                        .and(board.category.eq(Category.BD1))
                )
                .groupBy(member.id)
                .orderBy(likes.count().desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<LikePopularWeekRepositoryRespDto> findTopRepositoryByLikesBetween(
            LocalDateTime startDateTime, LocalDateTime endDateTime, int limit
    ) {
        return queryFactory.select(
                        new QLikePopularWeekRepositoryRespDto(
                                board.id, board.title, board.content, member.login, likes.count()
                        )
                )
                .from(likes)
                .join(likes.board, board)
                .join(board.member, member)
                .where(likes.createdDateTime.between(startDateTime, endDateTime)
                        .and(board.category.eq(Category.BD2))
                )
                .groupBy(board.id, board.title, board.content, member.login)
                .orderBy(likes.count().desc())
                .limit(limit)
                .fetch();
    }
}
