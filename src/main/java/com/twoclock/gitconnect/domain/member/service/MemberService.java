package com.twoclock.gitconnect.domain.member.service;

import com.twoclock.gitconnect.domain.board.dto.BoardResponseDto.BoardRespDto;
import com.twoclock.gitconnect.domain.board.service.BoardService;
import com.twoclock.gitconnect.domain.comment.dto.MyCommentRespDto;
import com.twoclock.gitconnect.domain.comment.service.CommentService;
import com.twoclock.gitconnect.domain.like.dto.LikesRespDto;
import com.twoclock.gitconnect.domain.like.service.LikeService;
import com.twoclock.gitconnect.domain.member.dto.MyProfileRespDto;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.global.model.PagingResponse;
import com.twoclock.gitconnect.openapi.github.dto.MemberGithubInfoDto;
import com.twoclock.gitconnect.openapi.github.dto.MemberInfoRespDto;
import com.twoclock.gitconnect.openapi.github.dto.RepositoryRespDto;
import com.twoclock.gitconnect.openapi.github.service.GitHubTokenRedisService;
import com.twoclock.gitconnect.openapi.github.service.GithubAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final GitHubTokenRedisService gitHubTokenRedisService;
    private final GithubAPIService githubAPIService;
    private final BoardService boardService;
    private final CommentService commentService;
    private final LikeService likeService;

    @Cacheable(value = "getMyInfo", key = "'my-info:github-id:' + #gitHubId", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public MyProfileRespDto getMyInfo(String gitHubId) {
        Member member = validateMember(gitHubId);
        String name = member.getLogin();
        String githubAccessToken = gitHubTokenRedisService.getGitHubToken(gitHubId).accessToken();

        MemberGithubInfoDto info = githubAPIService.getGitHubMemberInfo(githubAccessToken, member.getLogin());
        /**
         * TODO: 추후 사용 시 주석 해제 필요
         * -> 팔로워/팔로잉 목록 페이지 확인 시 사용
         */
//        List<FollowRespDto> followings = githubAPIService.getFollowing(githubAccessToken);
//        List<FollowRespDto> followers = githubAPIService.getFollowers(githubAccessToken);
        List<RepositoryRespDto> repositories = githubAPIService.getRepositories(githubAccessToken, name);

        PagingResponse<List<BoardRespDto>> boards = boardService.getMyBoardList(gitHubId, 1, 10);
        PagingResponse<List<MyCommentRespDto>> comments = commentService.getMyComments(gitHubId, 1, 10);
        PagingResponse<List<LikesRespDto>> likes = likeService.getLikesByGithubId(gitHubId, 1, 10);

        return new MyProfileRespDto(info, repositories, boards.listData(), comments.listData(), likes.listData());
    }

    @Cacheable(value = "getMemberInfo", key = "'user-info:github-id:' + #userGitHubId", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public MemberInfoRespDto getMemberInfo(String gitHubId, String userGitHubId) {
        Member userInfo = validateUserLoginName(userGitHubId);

        String githubAccessToken = gitHubTokenRedisService.getGitHubToken(gitHubId).accessToken();
        MemberGithubInfoDto info = githubAPIService.getGitHubMemberInfo(githubAccessToken, userGitHubId);

        List<RepositoryRespDto> repositories = githubAPIService.getRepositories(githubAccessToken, userGitHubId);

        return new MemberInfoRespDto(info, repositories);
    }

    @Transactional
    public void useServiceAgree(String gitHubId) {
        Member userInfo = validateMember(gitHubId);
        userInfo.agree();
    }

    private Member validateMember(String gitHubId) {
        return memberRepository.findByGitHubId(gitHubId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }

    private Member validateUserLoginName(String login) {
        return memberRepository.findByLogin(login).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }
}
