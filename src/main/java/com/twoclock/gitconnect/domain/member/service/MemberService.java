package com.twoclock.gitconnect.domain.member.service;

import com.twoclock.gitconnect.domain.member.dto.MemberProfileResponseDto;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Cacheable(value = "member-profile", key = "#gitHubId", unless = "#result == null")
    @Transactional(readOnly = true)
    public MemberProfileResponseDto getMember(String gitHubId) {
        Member member = validateMember(gitHubId);
        return new MemberProfileResponseDto(member);
    }

    private Member validateMember(String gitHubId) {
        return memberRepository.findByGitHubId(gitHubId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
    }
}
