package com.twoclock.gitconnect.domain.member.web;

import com.twoclock.gitconnect.domain.member.dto.MemberRequestDto.MemberModifyReqDto;
import com.twoclock.gitconnect.domain.member.dto.MemberResponseDto.MemberModifyRespDto;
import com.twoclock.gitconnect.domain.member.service.MemberService;
import com.twoclock.gitconnect.global.model.RestResponse;
import com.twoclock.gitconnect.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public RestResponse modify(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestPart MemberModifyReqDto requestDto,
            @RequestPart(required = false) MultipartFile multipartFile
    ) {
        String gitHubId = userDetails.getUsername();
        MemberModifyRespDto responseDto = memberService.modify(gitHubId, requestDto, multipartFile);
        return new RestResponse(responseDto);
    }
}
