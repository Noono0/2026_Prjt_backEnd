package com.noonoo.prjtbackend.codeGroup.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CodeGroupSearchCondition extends PageRequest {
    private String codeGroupId;
    private String codeGroupName;
    private String useYn;
    private String status;

    public CodeGroupSearchCondition() {
        setSortBy("codeGroupSeq");
        setSortDir("desc");
    }
}