package com.noonoo.prjtbackend.codeGroup.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CodeDetailSearchCondition extends PageRequest {
    private Long codeGroupSeq;
    private Long parentDetailSeq;
    private Integer codeLevel;
    private String codeId;
    private String codeValue;
    private String codeName;
    private String useYn;
    private String status;

    public CodeDetailSearchCondition() {
        setSortBy("sortOrder");
        setSortDir("asc");
    }
}