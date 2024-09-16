package com.twoclock.gitconnect.domain.comment.service;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.comment.dto.CommentListRespDto;
import com.twoclock.gitconnect.domain.comment.dto.CommentModifyReqDto;
import com.twoclock.gitconnect.domain.comment.dto.CommentRegistReqDto;
import com.twoclock.gitconnect.domain.comment.entity.Comment;
import com.twoclock.gitconnect.domain.comment.repository.CommentRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.domain.notification.service.NotificationService;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.global.model.Pagination;
import com.twoclock.gitconnect.global.model.PagingResponse;
import com.twoclock.gitconnect.global.util.PaginationUtil;
import com.vane.badwordfiltering.BadWordFiltering;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final NotificationService notificationService;

    @Transactional
    public void saveComment(CommentRegistReqDto dto, String githubId, Long boardId) {
        filteringBadWord(dto.content());
        Board board = validateBoard(boardId);
        Member member = validateMember(githubId);

        Comment comment = Comment.builder()
                .board(board)
                .member(member)
                .nickname(member.getName())
                .content(dto.content())
                .build();
        commentRepository.save(comment);
        notificationService.addCommentNotification(member);
    }

    @Transactional
    public void modifyComment(CommentModifyReqDto dto, String githubId, Long commentId) {
        filteringBadWord(dto.content());
        Member member = validateMember(githubId);
        Comment comment = validateComment(commentId);
        comment.checkUserId(member.getId());

        comment.update(dto.content());
    }

    @Transactional
    public void deleteComment(String githubId, Long commentId) {
        Member member = validateMember(githubId);
        Comment comment = validateComment(commentId);
        comment.checkUserId(member.getId());

        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public PagingResponse<List<CommentListRespDto>> getComments(Long boardId, int page, int size) {
        Board board = validateBoard(boardId);
        Pageable pageable = createPageable(page, size);

        Page<Comment> comments = commentRepository.findAllByBoard(board, pageable);
        List<CommentListRespDto> result = mapCommentsToDto(comments);

        Pagination pagination = PaginationUtil.pageInfo(comments.getTotalElements(), page, size);

        return PagingResponse.<List<CommentListRespDto>>builder()
                .listData(result)
                .pagination(pagination)
                .build();
    }

    private Board validateBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_BOARD)
        );
    }

    private Member validateMember(String githubId) {
        return memberRepository.findByGitHubId(githubId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }

    private Comment validateComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_COMMENT)
        );
    }

    private void filteringBadWord(String content) {
        BadWordFiltering filtering = new BadWordFiltering();
        if (filtering.check(content)) {
            throw new CustomException(ErrorCode.BAD_WORD);
        }
    }

    private Pageable createPageable(int page, int size) {
        int validatedPage = Math.max(0, page - 1);
        return PageRequest.of(validatedPage, size, Sort.by("createdDateTime").descending());
    }

    private List<CommentListRespDto> mapCommentsToDto(Page<Comment> comments) {
        return comments.stream()
                .map(CommentListRespDto::new)
                .toList();
    }
}
