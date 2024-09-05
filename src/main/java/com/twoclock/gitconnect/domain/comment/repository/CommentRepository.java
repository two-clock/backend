package com.twoclock.gitconnect.domain.comment.repository;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByBoard(Board board);

    Page<Comment> findByBoardId(Long boardId, PageRequest createdDateTime);
}
