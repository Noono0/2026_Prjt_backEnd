package com.noonoo.prjtbackend.common.commonDto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseAuditDto {
    private String createId;
    private String createIp;
    private String createDt;

    private String modifyId;
    private String modifyIp;
    private String modifyDt;

    private String status;
}