package com.twoclock.gitconnect.domain.board.repository;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BoardRepository extends JpaRepository<Board, Long>,
        CustomBoardRepository,
        QuerydslPredicateExecutor<Board> {

    Page<Board> findByCategoryOrderByCreatedDateTimeDesc(Category category, PageRequest pageRequest);
}
