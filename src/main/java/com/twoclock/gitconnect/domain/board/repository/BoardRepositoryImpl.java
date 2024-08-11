package com.twoclock.gitconnect.domain.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twoclock.gitconnect.domain.board.dto.QSearchResponseDto;
import com.twoclock.gitconnect.domain.board.dto.SearchRequestDto;
import com.twoclock.gitconnect.domain.board.dto.SearchResponseDto;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.member.dto.QMemberInfoDto;
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
                        new QMemberInfoDto(board.member.login, board.member.avatarUrl, board.member.name)
                ))
                .from(board)
                .join(board.member, member)
                .where(searchQueryBuilder(searchRequestDto))
                .orderBy(board.createdDateTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<SearchResponseDto> content = result.getResults();
        long total = result.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanBuilder searchQueryBuilder(SearchRequestDto searchRequestDto) {
        BooleanBuilder builder = new BooleanBuilder();

        // 게시판 카테고리 설정
        builder.and(board.category.eq(Category.of(searchRequestDto.category())));

        // 검색어가 존재하는 경우
        if(searchRequestDto.word() != null) {
            switch (searchRequestDto.type()) {
                case "title":
                    builder.and(board.title.contains(searchRequestDto.word()));
                    break;
                case "content":
                    builder.and(board.content.contains(searchRequestDto.word()));
                    break;
                case "name":
                    builder.and(board.nickname.contains(searchRequestDto.word()));
                    break;
                case "all":
                    builder.and(
                            board.title.contains(searchRequestDto.word())
                                    .or(board.content.contains(searchRequestDto.word()))
                                    .or(board.nickname.contains(searchRequestDto.word()))
                    );
                    break;
            }
        }

        return builder;
    }


}
