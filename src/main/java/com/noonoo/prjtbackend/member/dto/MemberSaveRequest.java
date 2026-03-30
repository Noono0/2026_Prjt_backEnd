package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class MemberSaveRequest {
    private Long memberSeq;
    private String birthYmd;
    private String createDt;
    private String createId;
    private String createIp;
    private String email;
    /** 레거시·호환: base64(data:) 는 저장 거부 — {@link #profileImageFileSeq} 사용 */
    private String profileImageUrl;
    /** 프로필 이미지 attach_file.file_seq (업로드 API 선행) */
    private Long profileImageFileSeq;
    private String gender;
    private String memberId;
    private String memberName;
    private String nickname;
    private String memberPwd;
    private String modifyDt;
    private String modifyId;
    private String modifyIp;
    private String phone;
    private String gradeCode;
    private String statusCode;
    /** 저장 시 MEMBER_ROLE 전체 교체 또는 신규 시 부여할 코드 목록 */
    private List<String> roleCodes = new ArrayList<>();
    /** 스트리머·컴퍼니 프로필 (null 이면 변경 없음·신규 시 생략 가능) */
    private MemberStreamerProfileSaveRequest streamerProfile;
}
