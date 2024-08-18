package com.twoclock.gitconnect.domain.like.repository;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.like.entity.Like;
import com.twoclock.gitconnect.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByBoardAndMember(Board board, Member member);
}
