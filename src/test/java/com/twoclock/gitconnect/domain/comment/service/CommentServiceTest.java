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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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


    @Nested
    @DisplayName("Comment 등록 테스트")
    class SaveCommentTest {

        @DisplayName("댓글을 입력할 경우 정상적으로 등록되어야 한다.")
        @Test
        void saveComment() {
            // given
            String githubId = "githubId";
            Long boardId = 1L;
            CommentRegistReqDto dto = new CommentRegistReqDto("댓글 내용");
            Member boardOwner = mockMember("boardOwnerGithubId");
            Member member = mockMember(githubId);
            Board board = mockBoard(boardOwner);

            BDDMockito.given(memberRepository.findByGitHubId(githubId)).willReturn(Optional.ofNullable(member));
            BDDMockito.given(boardRepository.findById(boardId)).willReturn(Optional.ofNullable(board));

            // when
            commentService.saveComment(dto, githubId, boardId);

            // then
            verify(commentRepository, times(1)).save(any(Comment.class));
            verify(notificationService, times(1))
                    .addNotificationInfo(eq(board.getMember()), eq(NotificationType.COMMENT), eq(member.getLogin()));
        }
    }


}