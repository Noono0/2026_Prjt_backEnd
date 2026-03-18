package com.noonoo.prjtbackend.codeGroup.dto;

import lombok.Data;


@Data
public class CodeDetailSaveRequest {
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
    private String createId;
    private String createIp;
    private String modifyId;
    private String modifyIp;
    private String status;
}
