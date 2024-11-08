package com.twoclock.gitconnect.domain.board.repository;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends
        JpaRepository<Board, Long>,
        CustomBoardRepository,
        QuerydslPredicateExecutor<Board> {

    @Query("SELECT b FROM Board b " +
            "WHERE b.category = :category AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%) " +
            "ORDER BY b.createdDateTime DESC")
    Page<Board> findByKeywordAndCategory(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :boardId")
    void addViewCount(Long boardId);

    @Query("select b from Board b join fetch b.member left join fetch b.fileList left join fetch b.likeList where b.id = :boardId")
    Optional<Board> findBoardDetailById(@Param("boardId") Long boardId);

    Page<Board> findAllByMember(Member member, Pageable pageable);
}
