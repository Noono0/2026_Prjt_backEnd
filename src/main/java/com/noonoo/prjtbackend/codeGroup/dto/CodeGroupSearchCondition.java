package com.noonoo.prjtbackend.codeGroup.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class CodeGroupSearchCondition extends PageRequest {
    private Long codeGroupSeq;
    private String codeGroupId;
    private String codeGroupName;
    private String useYn;
    private String status;

    public CodeGroupSearchCondition() {
        setSortBy("codeGroupSeq");
        setSortDir("desc");
    }
}