package com.noonoo.prjtbackend.codeGroup.dto;


import com.noonoo.prjtbackend.common.commonDto.BaseAuditDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeDetailDto extends BaseAuditDto {
    private Long codeDetailSeq;
    private Long codeGroupSeq;
    private Long parentDetailSeq;

    private String codeId;
    private String codeValue;
    private String codeName;
    private Integer codeLevel;
    private String description;

    private Integer sortOrder;
    private String useYn;

    private String attr1;
    private String attr2;
    private String attr3;
}