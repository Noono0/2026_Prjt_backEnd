package com.noonoo.prjtbackend.codeGroup.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CodeDetailDto {
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

    private LocalDateTime createDt;
    private String createId;
    private String createIp;

    private LocalDateTime modifyDt;
    private String modifyId;
    private String modifyIp;

    private String status;

    // 화면 표시용 확장
    private String codeGroupId;
    private String codeGroupName;
    private String parentCodeName;
}