package com.twoclock.gitconnect.global.model;

import lombok.Builder;

@Builder
public record PagingResponse<T>(
        Pagination pagination,
        T listData
) {
}
