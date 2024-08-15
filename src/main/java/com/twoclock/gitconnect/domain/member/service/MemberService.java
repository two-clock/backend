package com.twoclock.gitconnect.domain.member.service;

import com.twoclock.gitconnect.domain.member.dto.MemberRequestDto.MemberModifyReqDto;
import com.twoclock.gitconnect.domain.member.dto.MemberResponseDto.MemberModifyRespDto;
import com.twoclock.gitconnect.domain.member.entity.Member;
import com.twoclock.gitconnect.domain.member.repository.MemberRepository;
import com.twoclock.gitconnect.global.exception.CustomException;
import com.twoclock.gitconnect.global.exception.constants.ErrorCode;
import com.twoclock.gitconnect.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private static final int MAX_AVATAR_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final List<String> PERMIT_AVATAR_IMAGE_TYPE = List.of(
            "image/jpeg", "image/jpg", "image/png"
    );

    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    @Transactional
    public MemberModifyRespDto modify(String gitHubId, MemberModifyReqDto requestDto, MultipartFile multipartFile) {
        Member member = memberRepository.findByGitHubId(gitHubId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        if (memberRepository.existsByLogin(requestDto.login())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_MEMBER);
        }

        String avatarImageUrl = member.getAvatarUrl();
        if (!multipartFile.isEmpty()) {
            String avatarImageKey = extractKeyFromUrl(member.getAvatarUrl());
            s3Service.deleteFile(avatarImageKey);
            avatarImageUrl = handleAvatarImage(multipartFile);
        }

        member.update(requestDto.login(), avatarImageUrl, requestDto.name());
        return new MemberModifyRespDto(member.getLogin(), avatarImageUrl, member.getName());
    }

    private String handleAvatarImage(MultipartFile multipartFile) {
        if (multipartFile.getSize() > MAX_AVATAR_IMAGE_SIZE) {
            throw new CustomException(ErrorCode.OVER_AVATAR_IMAGE_SIZE);
        }

        if (!PERMIT_AVATAR_IMAGE_TYPE.contains(multipartFile.getContentType())) {
            throw new CustomException(ErrorCode.INVALID_AVATAR_IMAGE_TYPE);
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        String randomImageUrl = UUID.randomUUID() + extension;

        try {
            return s3Service.uploadFile(randomImageUrl, multipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            return url.getPath().substring(1);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
