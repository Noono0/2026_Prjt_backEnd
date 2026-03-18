package com.noonoo.prjtbackend.codeGroup.dto;

import lombok.Data;

@Data
public class CodeGroupSaveRequest {
    private Long codeGroupSeq;
    private String codeGroupId;
    private String codeGroupName;
    private String description;
    private Integer sortOrder;
    private String useYn;

    private String createId;
    private String createIp;
    private String modifyId;
    private String modifyIp;
    private String status;
}