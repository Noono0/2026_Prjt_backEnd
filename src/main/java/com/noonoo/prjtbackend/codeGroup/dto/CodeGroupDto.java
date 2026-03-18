package com.noonoo.prjtbackend.codeGroup.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CodeGroupDto {
    private Long codeGroupSeq;
    private String codeGroupId;
    private String codeGroupName;
    private String description;
    private Integer sortOrder;
    private String useYn;

    private LocalDateTime createDt;
    private String createId;
    private String createIp;

    private LocalDateTime modifyDt;
    private String modifyId;
    private String modifyIp;

    private String status;
}