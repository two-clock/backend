package com.twoclock.gitconnect.global.dummy;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.entity.constants.Role;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class DummyDevlnit {

    // 프로덕트 출시 전에 개발자들이 사용할 더미 데이터를 초기화하는 클래스
    @Profile("local")
    @Bean
    CommandLineRunner init(MemberRepository memberRepository, BoardRepository boardRepository) {
        return (args) -> {
            log.info("Dummy Data Init");

            // 테스트 계정 생성
            Member member = Member.builder()
                    .login("loginId")
                    .avatarUrl("/uploads/profile/test.jpg")
                    .name("테스트 유저")
                    .role(Role.ROLE_USER)
                    .build();
            memberRepository.save(member);

            // 테스트 게시글 생성
            List<Board> boards = new ArrayList<>();
            createDummyBoards(boards, "계정 홍보 테스트 게시글", Category.BD1, 10, member);
            createDummyBoards(boards, "저장소 홍보 테스트 게시글", Category.BD2, 10, member);
            createDummyBoards(boards, "사용자 신고 테스트 게시글", Category.BD3, 10, member);
            boardRepository.saveAll(boards);
        };
    }

    private void createDummyBoards(List<Board> boards, String titlePrefix, Category category,
                                   int count, Member member){
        for (int i = 0; i < count; i++) {
            Board board = Board.builder()
                    .title(titlePrefix + i)
                    .content("테스트 게시글 내용" + i)
                    .nickname("테스트 유저")
                    .member(member)
                    .category(category)
                    .build();
            boards.add(board);
        }
    }

}
