package com.twoclock.gitconnect.domain.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.twoclock.gitconnect.domain.board.dto.QSearchResponseDto;
import com.twoclock.gitconnect.domain.board.dto.SearchRequestDto;
import com.twoclock.gitconnect.domain.board.dto.SearchResponseDto;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.member.dto.QMemberLoginRespDto;
import com.twoclock.gitconnect.domain.member.entity.constants.Role;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.ExpressionUtils.count;
import static com.twoclock.gitconnect.domain.board.entity.QBoard.board;
import static com.twoclock.gitconnect.domain.comment.entity.QComment.comment;
import static com.twoclock.gitconnect.domain.like.entity.QLikes.likes;
import static com.twoclock.gitconnect.domain.member.entity.QMember.member;

public class BoardRepositoryImpl implements CustomBoardRepository {

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
                        new QMemberLoginRespDto(board.member.login, board.member.gitHubId, board.member.avatarUrl, board.member.name),
                        comment.id.countDistinct(),
                        likes.id.countDistinct()
                ))
                .from(board)
                .join(board.member, member)
                .leftJoin(comment).on(board.id.eq(comment.board.id))
                .leftJoin(likes).on(board.id.eq(likes.board.id))
                .where(searchQueryBuilder(searchRequestDto))
                .groupBy(board.id)
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
        Category category = Category.of(searchRequestDto.category());
        String roleStr = Optional.ofNullable(searchRequestDto.role()).orElse("ROLE_USER");
        Role role = Role.of(roleStr);

        // 게시판 카테고리 설정
        builder.and(board.category.eq(category));

        // 검색어가 존재하는 경우
        if (searchRequestDto.word() != null) {
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
        // 신고 게시판인 경우 추가 조건처리
        if(category.equals(Category.BD3) && !Role.ROLE_ADMIN.equals(role)) {
            builder.and(board.member.gitHubId.eq(searchRequestDto.githubId()));
        }

        return builder;
    }


}
