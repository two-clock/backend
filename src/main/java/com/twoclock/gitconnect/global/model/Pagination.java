package com.twoclock.gitconnect.global.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pagination {
    // 페이징 관련
    private int currentPageNo;        // 현재 페이지 번호 (열어볼 페이지)

    private int sizePerPage = 10;    // 한 페이지당 게시되는 게시물 건 수
    private long totalCnt;            // 전체 게시물 건 수
    private int pageCnt;            // 페이지 개수

    private int startRowNum;        // 시작 row 번호 (1부터 시작)
    private int range = -1;            // 페이지 범위 선택 (페이지 사이즈 10일때 ex 1:1~10, 2:11~20 ...)
    private int pageSize = 10;        // 페이지 사이즈 단위 (보여지는 view 단에서 보여지는 페이지 (1~10 or 1~5 등등)

    private int startPageNo;        // 시작 페이지 번호 (페이지 범위 내)
    private int endPageNo;            // 끝 페이지 번호 (페이지 범위 내)

    private boolean prev;            // 이전 페이지 여부
    private boolean next;            // 다음 페이지 여부
}
