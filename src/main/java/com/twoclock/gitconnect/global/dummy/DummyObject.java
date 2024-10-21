package com.twoclock.gitconnect.global.dummy;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.comment.dto.CommentRegistReqDto;
import com.twoclock.gitconnect.domain.comment.entity.Comment;
import com.twoclock.gitconnect.domain.member.entity.Member;

import static com.twoclock.gitconnect.domain.member.entity.constants.Role.ROLE_USER;

public class DummyObject {

    /**
     * Test Code 작성 시 Dummy Data 생성을 위한 Class
     * method 앞에 `mock`이 붙으면 가짜 객체 생성
     * method 앞에 `new`가 붙으면 실제 객체 생성
     */

    protected Member mockMember(String gitHubId) {
        return new Member(
                "mockUser",
                gitHubId,
                "testUrl",
                "유저1",
                ROLE_USER
        );
    }

    protected Board mockBoard(Member member) {
        return new Board(
                member,
                member.getName(),
                Category.BD1,
                "테스트 게시글",
                "테스트 게시글 내용"
        );
    }

}
