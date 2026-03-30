package com.noonoo.prjtbackend.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardPopularConfigDto {
    /** 추천 수가 이 값 이상이면 인기글로 표시 */
    private int threshold;
    /** 제목 옆 뱃지 문구 (공통코드 code_name 등) */
    private String badgeLabel;
}
