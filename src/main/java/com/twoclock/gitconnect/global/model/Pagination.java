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

    // pageInfo
    public void setPageInfo(int currentPageNo, int sizePerPage, int range, int pageSize, long totalCnt) {

        if (totalCnt > 0) {
            if (currentPageNo == 0) {
                currentPageNo = 1;
            }

            this.currentPageNo = currentPageNo;
            this.sizePerPage = sizePerPage;
            this.range = range;
            this.pageSize = pageSize;
            this.totalCnt = totalCnt;

            // 전체 페이지 수
            this.pageCnt = calcPage(totalCnt, sizePerPage);

            // current page 번호가 전체 페이지 수보다 클 경우 전체 페이지 수로 설정
            if (this.currentPageNo > this.pageCnt) {
                this.currentPageNo = this.pageCnt;
            }

            // range 가 주어지지 않는 경우
            // current page 와 page size 를 이용하여 range 를 계산
            if (this.range == -1) {
                this.range = calcRange(currentPageNo, pageSize);
                if (this.range == 0) {
                    this.range = 1;
                }
            }

            // 시작 row 번호
            this.startRowNum = (this.currentPageNo - 1) * sizePerPage + 1;
            // 시작 페이지 번호
            this.startPageNo = (this.range - 1) * pageSize + 1;
            // 끝 페이지 번호
            this.endPageNo = this.range * pageSize;

            // 끝 페이지 번호가 전체 페이지 수보다 클 경우 전체 페이지 수로 설정
            if (this.endPageNo > this.pageCnt) {
                this.endPageNo = this.pageCnt;
            }

            // 이전 페이지 여부
            this.prev = currentPageNo != 1;
            // 다음 페이지 여부
            this.next = currentPageNo != endPageNo;

        } else {
            // 데이터가 없는 경우
            this.totalCnt = 0;
            this.pageCnt = 0;
            this.startRowNum = 0;
            this.startPageNo = 0;
            this.endPageNo = 0;
            this.prev = false;
            this.next = false;
        }

    }

    public void setPageInfo(long totalCnt, int currentPageNo, int sizePerPage) {
        setPageInfo(currentPageNo, sizePerPage, this.range, this.pageSize, totalCnt);
    }

    private int calcPage(long totalCnt, int sizePerPage) {
        return (int) Math.ceil((double) totalCnt / sizePerPage);
    }

    private int calcRange(int currentPageNo, int pageSize) {
        return (int) Math.ceil((double) currentPageNo / pageSize);
    }
}
