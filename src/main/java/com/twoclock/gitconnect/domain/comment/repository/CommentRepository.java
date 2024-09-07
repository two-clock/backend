package com.twoclock.gitconnect.domain.comment.repository;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByBoard(Board board, Pageable pageable);
}
