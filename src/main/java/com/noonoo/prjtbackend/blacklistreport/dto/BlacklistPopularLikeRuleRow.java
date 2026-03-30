package com.noonoo.prjtbackend.blacklistreport.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 블랙리스트 목록 조회 그룹(A0006) 중 추천 수 임계 탭(A00052, A00053 등) 한 건.
 */
@Getter
@Setter
public class BlacklistPopularLikeRuleRow {
    private String codeId;
    private String attr1;
    private String codeName;
    private String codeValue;
}
