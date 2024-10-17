package com.twoclock.gitconnect.global.dummy;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.member.entity.Member;
import net.datafaker.Faker;

import static com.twoclock.gitconnect.domain.member.entity.constants.Role.ROLE_USER;

public class DummyObject {

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
