package com.twoclock.gitconnect.domain.board.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twoclock.gitconnect.domain.board.dto.QSearchResponseDto;
import com.twoclock.gitconnect.domain.board.dto.SearchRequestDto;
import com.twoclock.gitconnect.domain.board.dto.SearchResponseDto;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.twoclock.gitconnect.domain.board.entity.QBoard.board;
import static com.twoclock.gitconnect.domain.member.entity.QMember.member;

public class BoardRepositoryImpl implements CustomBoardRepository{

    private final JPAQueryFactory queryFactory;

    public BoardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<SearchResponseDto> searchBoardList(SearchRequestDto searchRequestDto, Pageable pageable) {
        QueryResults<SearchResponseDto> result = queryFactory
                .select(new QSearchResponseDto(
                        board.id,
                        board.title,
                        board.content,
                        board.nickname,
                        board.category.stringValue(),
                        board.member
                ))
                .from(board)
                .join(board.member, member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<SearchResponseDto> content = result.getResults();
        long total = result.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
}
