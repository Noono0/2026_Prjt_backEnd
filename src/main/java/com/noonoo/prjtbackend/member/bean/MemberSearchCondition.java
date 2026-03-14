package com.noonoo.prjtbackend.member.bean;

import lombok.Getter;
import lombok.Setter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSearchCondition {
    private String memberId;
    private String memberName;
    private String roleCode;
    private String status;

    private String sortBy = "memberSeq";
    private String sortDir = "desc";

    private String createDt;
    private String modifyDt;

    /** 현재 페이지 (1부터 시작) */
    private Integer page = 1;

    /** 페이지당 건수 */
    private Integer size = 20;

    public int getOffset() {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 20 : size;
        return (p - 1) * s;
    }

    public int getSafeSize() {
        return (size == null || size < 1) ? 20 : size;
    }
}
