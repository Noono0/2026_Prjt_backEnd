package com.noonoo.prjtbackend.common.paging;

import java.util.List;

public final class PagingUtils {

    private PagingUtils() {}

    public static <T> PageResponse<T> toPageResponse(
            PageRequest request, long totalCount, List<T> items) {
        return PageResponse.<T>builder()
                .items(items)
                .page(request.getSafePage())
                .size(request.getSafeSize())
                .totalCount(totalCount)
                .build();
    }
}
