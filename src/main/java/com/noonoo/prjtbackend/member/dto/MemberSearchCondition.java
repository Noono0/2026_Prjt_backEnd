package com.noonoo.prjtbackend.member.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class MemberSearchCondition extends PageRequest {
    private String memberId;
    private String memberName;
    /** MEMBER_ROLE 존재 여부 필터 */
    private String roleCode;
    private String gradeCode;
    private String statusCode;

    public MemberSearchCondition() {
        setSortBy("memberSeq");
        setSortDir("desc");
    }
}
