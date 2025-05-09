package com.twoclock.gitconnect.domain.comment.service;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.comment.dto.CommentRegistReqDto;
import com.twoclock.gitconnect.domain.comment.entity.Comment;
import com.twoclock.gitconnect.domain.comment.repository.CommentRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.domain.notification.entity.constants.NotificationType;
import com.twoclock.gitconnect.domain.notification.service.NotificationService;
import com.twoclock.gitconnect.global.dummy.DummyObject;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@ExtendWith(MockitoExtension.class)
class CommentServiceTest extends DummyObject {

    @InjectMocks
    CommentService commentService;
    @Mock
    NotificationService notificationService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    BoardRepository boardRepository;

    private CommentRegistReqDto dto;
    private Member member;
    private Board board;
    private String githubId;
    private Long boardId;



    @Nested
    @DisplayName("Comment 등록 테스트")
    class SaveCommentTest {

        @BeforeEach
        void setUp() {
            githubId = "githubId";
            boardId = 1L;
            dto = new CommentRegistReqDto("댓글 내용");
            member = mockMember(githubId);
            board = mockBoard(member);
        }

        @DisplayName("댓글을 입력할 경우 정상적으로 등록되어야 한다.")
        @Test
        void saveComment() {
            // given & stub
            BDDMockito.given(memberRepository.findByGitHubId(githubId)).willReturn(Optional.ofNullable(member));
            BDDMockito.given(boardRepository.findById(boardId)).willReturn(Optional.ofNullable(board));

            // when
            commentService.saveComment(dto, githubId, boardId);

            // then
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @DisplayName("댓글을 입력할 경우 작성자와 게시글 작성자가 동일한 경우 알람이 발송되지 않아야 한다.")
        @Test
        void notSentNotify() {
            // given & stub
            BDDMockito.given(memberRepository.findByGitHubId(githubId)).willReturn(Optional.ofNullable(member));
            BDDMockito.given(boardRepository.findById(boardId)).willReturn(Optional.ofNullable(board));

            // when
            commentService.saveComment(dto, githubId, boardId);

            // then
            verify(notificationService, times(0))
                    .addNotificationInfo(eq(board.getMember()), eq(NotificationType.COMMENT), eq(member.getLogin()));
        }

        @DisplayName("댓글을 입력할 경우 작성자와 게시글 작성자가 동일하지 않은 경우 알람이 발송되어야 한다.")
        @Test
        void sentNotify() {
            // given
            Member boardOwner = mockMember("boardOwnerGithubId");
            board = mockBoard(boardOwner);

            // stub
            BDDMockito.given(memberRepository.findByGitHubId(githubId)).willReturn(Optional.ofNullable(member));
            BDDMockito.given(boardRepository.findById(boardId)).willReturn(Optional.ofNullable(board));

            // when
            commentService.saveComment(dto, githubId, boardId);

            // then
            verify(notificationService, times(1))
                    .addNotificationInfo(eq(board.getMember()), eq(NotificationType.COMMENT), eq(member.getLogin()));
        }

        @DisplayName("댓글을 입력할 경우 비속어가 포함되어 있을 경우 에러가 발생해야 한다.")
        @Test
        void badWordError() {
            // given
            dto = new CommentRegistReqDto("시발");

            // when & then
            assertThatThrownBy(() -> commentService.saveComment(dto, githubId, boardId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.BAD_WORD.getMessage());
        }

        @DisplayName("댓글을 입력할 경우 게시글이 존재하지 않을 경우 에러가 발생해야 한다.")
        @Test
        void notFoundBoardError() {
            // given & stub
            BDDMockito.given(boardRepository.findById(boardId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.saveComment(dto, githubId, boardId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.NOT_FOUND_BOARD.getMessage());
        }

        @DisplayName("존재하지 않는 사용자 Key로 댓글을 입력할 경우 에러가 발생해야 한다.")
        @Test
        void notLoginError() {
            // given & stub
            BDDMockito.given(boardRepository.findById(boardId)).willReturn(Optional.ofNullable(board));
            BDDMockito.given(memberRepository.findByGitHubId(githubId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> commentService.saveComment(dto, githubId, boardId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ErrorCode.NOT_FOUND_MEMBER.getMessage());
        }
    }


}