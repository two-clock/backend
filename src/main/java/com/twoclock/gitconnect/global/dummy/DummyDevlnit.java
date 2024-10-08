package com.twoclock.gitconnect.global.dummy;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.entity.constants.Category;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.comment.entity.Comment;
import com.twoclock.gitconnect.domain.comment.repository.CommentRepository;
import com.twoclock.gitconnect.domain.like.entity.Likes;
import com.twoclock.gitconnect.domain.like.repository.LikeRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.domain.notification.entity.Notification;
import com.twoclock.gitconnect.domain.notification.entity.constants.NotificationType;
import com.twoclock.gitconnect.domain.notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.twoclock.gitconnect.domain.member.entity.constants.Role.ROLE_USER;

@Slf4j
@Configuration
public class DummyDevlnit {

    // 프로덕트 출시 전에 개발자들이 사용할 더미 데이터를 초기화하는 클래스
    @Profile("local")
    @Bean
    CommandLineRunner init(MemberRepository memberRepository,
                           BoardRepository boardRepository,
                           CommentRepository commentRepository,
                           NotificationRepository notificationRepository, LikeRepository likeRepository) {
        return (args) -> {
            log.info("Dummy Data Init");
            Faker faker = new Faker();

            // 테스트 계정 생성
            List<Member> members = Arrays.asList(
                    new Member("loginId-1", "1234", faker.avatar().image(), "테스트 유저 1", ROLE_USER),
                    new Member("loginId-2", "5678", faker.avatar().image(), "테스트 유저 2", ROLE_USER),
                    new Member("loginId-3", "0000", faker.avatar().image(), "테스트 유저 3", ROLE_USER)
            );
            memberRepository.saveAll(members);

            // 테스트 게시글 생성
            List<Board> boards = new ArrayList<>();
            createDummyBoards(boards, "계정 홍보 테스트 게시글", Category.BD1, 50, members.get(0));
            createDummyBoards(boards, "계정 홍보 테스트 게시글2", Category.BD1, 50, members.get(1));
            createDummyBoards(boards, "저장소 홍보 테스트 게시글", Category.BD2, 50, members.get(0));
            createDummyBoards(boards, "저장소 홍보 테스트 게시글2", Category.BD2, 50, members.get(1));
            createDummyBoards(boards, "사용자 신고 테스트 게시글", Category.BD3, 100, members.get(0));
            boardRepository.saveAll(boards);

            // 테스트 댓글 생성
            List<Comment> comments = new ArrayList<>();
            createDummyComments(comments, 5, members.get(0), boards.get(0));
            commentRepository.saveAll(comments);

            // 테스트 좋아요 생성
            List<Likes> likes = new ArrayList<>();

            IntStream.range(0, 5)
                    .forEach(i -> createDummyLike(likes, members.get(1), boards.get(i), LocalDateTime.now()));
            IntStream.range(50, 60)
                    .forEach(i -> createDummyLike(likes, members.get(0), boards.get(i), LocalDateTime.now()));

            createDummyLike(likes, members.get(0), boards.get(150), LocalDateTime.now());
            createDummyLike(likes, members.get(1), boards.get(100), LocalDateTime.now());
            createDummyLike(likes, members.get(2), boards.get(100), LocalDateTime.now());

            likeRepository.saveAll(likes);

            // 테스트 알림 생성
            List<Notification> notification = new ArrayList<>();
            createDummyNotifications(notification, 5, members.get(0));
            notificationRepository.saveAll(notification);
        };
    }

    private void createDummyBoards(List<Board> boards, String titlePrefix, Category category,
                                   int count, Member member) {
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

    private void createDummyComments(List<Comment> comments, int count,
                                     Member member, Board board) {
        for (int i = 0; i < count; i++) {
            Comment comment = Comment.builder()
                    .board(board)
                    .member(member)
                    .nickname(member.getName())
                    .content("테스트 댓글 입니다 " + i)
                    .build();
            comments.add(comment);
        }
    }

    private void createDummyLike(List<Likes> likes, Member member, Board board, LocalDateTime createDateTime) {
        Likes like = Likes.builder()
                .board(board)
                .member(member)
                .createdDateTime(createDateTime)
                .build();

        likes.add(like);
    }

    private void createDummyNotifications(List<Notification> notifications, int count,
                                          Member member) {
        for (int i = 0; i < count; i++) {
            Notification notification = Notification.builder()
                    .member(member)
                    .message("테스트 알림 메시지" + i)
                    .type(NotificationType.CHAT)
                    .build();
            notifications.add(notification);
        }
    }

}
