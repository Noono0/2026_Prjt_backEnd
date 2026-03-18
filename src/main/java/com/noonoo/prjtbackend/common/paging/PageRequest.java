package com.noonoo.prjtbackend.common.paging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class PageRequest {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 200;

    private int page = DEFAULT_PAGE;
    private int size = DEFAULT_SIZE;
    private String sortBy;
    private String sortDir = "desc";

    public int getSafePage() {
        return page <= 0 ? DEFAULT_PAGE : page;
    }

    public int getSafeSize() {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    public int getOffset() {
        return (getSafePage() - 1) * getSafeSize();
    }

    public String getSafeSortDir() {
        if (!StringUtils.hasText(sortDir)) {
            return "desc";
        }
        return "asc".equalsIgnoreCase(sortDir) ? "asc" : "desc";
    }

    public boolean hasSortBy() {
        return StringUtils.hasText(sortBy);
    }
}