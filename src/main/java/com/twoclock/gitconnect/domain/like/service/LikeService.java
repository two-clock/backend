package com.twoclock.gitconnect.domain.like.service;

import com.twoclock.gitconnect.domain.board.entity.Board;
import com.twoclock.gitconnect.domain.board.repository.BoardRepository;
import com.twoclock.gitconnect.domain.like.dto.LikePopularWeekMemberRespDto;
import com.twoclock.gitconnect.domain.like.dto.LikesRespDto;
import com.twoclock.gitconnect.domain.like.entity.Likes;
import com.twoclock.gitconnect.domain.like.repository.LikeRepository;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.global.model.Pagination;
import com.twoclock.gitconnect.global.model.PagingResponse;
import com.twoclock.gitconnect.global.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addLikeToBoard(Long boardId, String githubId) {
        Board board = validateBoard(boardId);
        Member member = validateMember(githubId);

        boolean duplicated = likeRepository.existsByBoardAndMember(board, member);
        if (duplicated) {
            throw new CustomException(ErrorCode.DUPLICATED_LIKE);
        }

        Likes likes = Likes.builder()
                .board(board)
                .member(member)
                .createdDateTime(LocalDateTime.now())
                .build();

        likeRepository.save(likes);
    }

    @Transactional
    public void deleteLikeToBoard(Long boardId, String githubId) {
        Board board = validateBoard(boardId);
        Member member = validateMember(githubId);
        Likes likes = likeRepository.findByBoardAndMember(board, member).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_LIKE)
        );
        likeRepository.delete(likes);
    }

    @Transactional(readOnly = true)
    public PagingResponse<List<LikesRespDto>> getLikesByBoardId(Long boardId, int page, int size) {
        Board board = validateBoard(boardId);
        Pageable pageable = createPageable(page, size);

        Page<Likes> likes = likeRepository.findAllByBoard(board, pageable);
        List<LikesRespDto> result = likes.stream().map(LikesRespDto::new).toList();

        Pagination pagination = PaginationUtil.pageInfo(likes.getTotalElements(), page, size);

        return PagingResponse.<List<LikesRespDto>>builder()
                .listData(result)
                .pagination(pagination)
                .build();
    }

    @Transactional(readOnly = true)
    public Boolean getLikeByBoardId(Long boardId, String gitHubId) {
        if (gitHubId == null) {
            return Boolean.FALSE;
        }

        Board board = validateBoard(boardId);
        Member member = validateMember(gitHubId);

        return likeRepository.existsByBoardAndMember(board, member);
    }

    @Transactional(readOnly = true)
    public List<LikePopularWeekMemberRespDto> getPopularWeekMember() {
        LocalDate now = LocalDate.now();

        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);

        LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endOfWeekDateTime = endOfWeek.atTime(23, 59, 59);

        int limit = 5;

        return likeRepository.findTopMemberByLikesBetween(startOfWeekDateTime, endOfWeekDateTime, limit);
    }

    @Transactional(readOnly = true)
    public Object getPopularWeekRepository() {
        LocalDate now = LocalDate.now();

        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);

        LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endOfWeekDateTime = endOfWeek.atTime(23, 59, 59);

        int limit = 10;

        return likeRepository.findTopRepositoryByLikesBetween(startOfWeekDateTime, endOfWeekDateTime, limit);
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

    private Pageable createPageable(int page, int size) {
        int validatedPage = Math.max(0, page - 1);
        return PageRequest.of(validatedPage, size, Sort.by("createdDateTime").descending());
    }
}
