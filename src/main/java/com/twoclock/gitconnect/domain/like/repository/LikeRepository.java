package com.twoclock.gitconnect.domain.like.repository;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.like.entity.Likes;
import com.twoclock.gitconnect.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long>, CustomLikeRepository {

    boolean existsByBoardAndMember(Board board, Member member);

    Optional<Likes> findByBoardAndMember(Board board, Member member);

    Page<Likes> findAllByBoard(Board board, Pageable pageable);

    Long countByBoard(Board board);
}
