package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberStreamerProfileSaveRequest {
    private String instagramUrl;
    private String youtubeUrl;
    private String soopChannelUrl;
    private String companyCategoryCode;
    private String bloodType;
    private String careerHistory;
}
