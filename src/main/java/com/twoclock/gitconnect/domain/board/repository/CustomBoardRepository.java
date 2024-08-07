package com.twoclock.gitconnect.domain.board.repository;


import com.twoclock.gitconnect.domain.board.dto.SearchRequestDto;
import com.twoclock.gitconnect.domain.board.dto.SearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface CustomBoardRepository {
    Page<SearchResponseDto> searchBoardList(SearchRequestDto searchRequestDto, Pageable pageRequest);
}
