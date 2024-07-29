package com.twoclock.gitconnect.domain.comment.repository;

import com.twoclock.gitconnect.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
