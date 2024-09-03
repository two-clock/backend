package com.twoclock.gitconnect.domain.board.repository;

import com.twoclock.gitconnect.domain.board.entity.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {
}
