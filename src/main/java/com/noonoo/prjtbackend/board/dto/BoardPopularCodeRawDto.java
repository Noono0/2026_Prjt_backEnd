package com.noonoo.prjtbackend.board.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 공통코드에서 인기글 설정 한 건을 읽을 때 사용하는 매핑용 DTO.
 * 대분류 그룹 ID A0001 + 중분류 코드 ID A00017 행.
 * 임계값은 attr1 → code_value → description 순으로 숫자 파싱을 시도한다.
 */
@Getter
@Setter
public class BoardPopularCodeRawDto {
    private String attr1;
    private String codeName;
    private String codeValue;
    private String description;
}
