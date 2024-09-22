package com.twoclock.gitconnect.domain.board.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @DisplayName("게시글 상세 조회 - N+1 문제 발생")
    @Test
    void getBoardDetail_01() {
        checkSystemTime(() -> boardService.getBoardDetail(1L, null));
    }

    private void checkSystemTime(Runnable task) {
        long startTime = System.currentTimeMillis();
        task.run();
        long endTime = System.currentTimeMillis();
        System.out.println("실행 시간: " + (endTime - startTime) + "ms");
    }
}