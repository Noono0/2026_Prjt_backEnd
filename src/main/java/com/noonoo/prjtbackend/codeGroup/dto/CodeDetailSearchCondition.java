package com.noonoo.prjtbackend.codeGroup.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class CodeDetailSearchCondition extends PageRequest {
    private Long codeDetailSeq;
    private Long codeGroupSeq;
    private Long parentDetailSeq;

    private String codeId;
    private String codeName;
    private Integer codeLevel;
    private String useYn;
    private String status;

    public CodeDetailSearchCondition() {
        setSortBy("codeDetailSeq");
        setSortDir("desc");
    }
}