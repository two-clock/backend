package com.twoclock.gitconnect.domain.board.repository;

import com.twoclock.gitconnect.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BoardRepository extends JpaRepository<Board, Long> ,
        CustomBoardRepository ,
        QuerydslPredicateExecutor<Board> {
}
