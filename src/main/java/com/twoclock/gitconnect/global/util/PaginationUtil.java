package com.twoclock.gitconnect.global.util;

import com.twoclock.gitconnect.global.model.Pagination;

public class PaginationUtil {
    public static Pagination pageInfo(Pagination pagination, int currentPageNo, int sizePerPage, int range, int pageSize, long totalCnt) {

        if (totalCnt > 0) {
            if (currentPageNo == 0) {
                currentPageNo = 1;
            }

            pagination.setCurrentPageNo(currentPageNo);
            pagination.setSizePerPage(sizePerPage);
            pagination.setRange(range);
            pagination.setPageSize(pageSize);
            pagination.setTotalCnt(totalCnt);

            // 전체 페이지 수 계산
            int pageCnt = calcPage(totalCnt, sizePerPage);
            pagination.setPageCnt(pageCnt);

            // current page 번호가 전체 페이지 수보다 클 경우 전체 페이지 수로 설정
            if (currentPageNo > pageCnt) {
                pagination.setCurrentPageNo(pageCnt);
            }

            // range가 주어지지 않는 경우 current page와 page size를 이용해 계산
            if (range == -1) {
                range = calcRange(currentPageNo, pageSize);
                if (range == 0) {
                    range = 1;
                }
                pagination.setRange(range);
            }

            // 시작 row 번호 계산
            pagination.setStartRowNum((currentPageNo - 1) * sizePerPage + 1);
            // 시작 페이지 번호 계산
            pagination.setStartPageNo((range - 1) * pageSize + 1);
            // 끝 페이지 번호 계산
            int endPageNo = range * pageSize;
            if(endPageNo > pageCnt){
                endPageNo = pageCnt;
            }
            pagination.setEndPageNo(endPageNo);

            // 이전 페이지 여부
            pagination.setPrev(currentPageNo != 1);
            // 다음 페이지 여부
            pagination.setNext(currentPageNo != endPageNo);

        } else {
            resetPagination(pagination);
        }
        return pagination;
    }

    // 총 페이지 수 계산
    private static int calcPage(long totalCnt, int sizePerPage) {
        return (int) Math.ceil((double) totalCnt / sizePerPage);
    }

    // 페이지 범위 계산
    private static int calcRange(int currentPageNo, int pageSize) {
        return (int) Math.ceil((double) currentPageNo / pageSize);
    }

    // 데이터를 초기화하는 메서드
    private static void resetPagination(Pagination pagination) {
        pagination.setTotalCnt(0);
        pagination.setPageCnt(0);
        pagination.setStartRowNum(0);
        pagination.setStartPageNo(0);
        pagination.setEndPageNo(0);
        pagination.setPrev(false);
        pagination.setNext(false);
    }

    // 간단한 pageInfo 설정 메서드
    public static Pagination pageInfo(long totalCnt, int currentPageNo, int sizePerPage) {
        Pagination pagination = new Pagination();
        return pageInfo(pagination, currentPageNo, sizePerPage, pagination.getRange(), pagination.getPageSize(), totalCnt);
    }
}