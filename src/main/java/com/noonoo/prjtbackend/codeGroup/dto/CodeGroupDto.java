package com.noonoo.prjtbackend.codeGroup.dto;

import com.noonoo.prjtbackend.common.commonDto.BaseAuditDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeGroupDto extends BaseAuditDto {
    private Long codeGroupSeq;
    private String codeGroupId;
    private String codeGroupName;
    private String description;
    private Integer sortOrder;
    private String useYn;
}