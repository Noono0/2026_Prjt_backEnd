package com.noonoo.prjtbackend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEmoticonDto {

    private Long memberEmoticonSeq;
    private Long memberSeq;
    private String imageUrl;
    private Integer sortOrder;
    private String createDt;
}
