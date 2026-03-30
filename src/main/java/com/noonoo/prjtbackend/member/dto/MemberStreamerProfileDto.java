package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;

/** 회원 1:1 스트리머·컴퍼니 확장 프로필 (member 테이블과 분리) */
@Getter
@Setter
public class MemberStreamerProfileDto {
    private Long memberStreamerProfileSeq;
    private Long memberSeq;
    private String instagramUrl;
    private String youtubeUrl;
    private String soopChannelUrl;
    /** 공통코드 code_value (컴퍼니/팀 구분 등) */
    private String companyCategoryCode;
    private String bloodType;
    /** 약력·이력 (자유 텍스트) */
    private String careerHistory;
}
