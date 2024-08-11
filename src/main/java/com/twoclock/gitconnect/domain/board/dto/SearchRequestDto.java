package com.twoclock.gitconnect.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.PageRequest;

public record SearchRequestDto(
        String word,
        String type, // 검색 조건(제목, 내용, 작성자)
        @NotBlank(message = "카테고리는 필수값 입니다.")
        String category,
        Long page,
        Long size

) {
    public PageRequest toPageRequest() {
        int pageNumber = (page != null) ? page.intValue() : 0;
        int pageSize = (size != null) ? size.intValue() : 10;
        return PageRequest.of(pageNumber, pageSize);
    }

}

