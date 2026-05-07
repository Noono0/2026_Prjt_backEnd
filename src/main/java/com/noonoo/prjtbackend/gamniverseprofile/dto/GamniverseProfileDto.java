package com.noonoo.prjtbackend.gamniverseprofile.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GamniverseProfileDto {
    private Long gamniverseProfileSeq;
    private String profileName;
    private Integer sortOrder;
    private String rankCode;
    private String affiliationCode;
    private String broadcastLink;
    private String soopBroadcastLink;
    private String instagramUrl;
    private String youtubeUrl;
    private String cafeLink;
    private Long profileImageFileSeq;
    private String profileRowsJson;
    private Boolean isLive;
    private String liveRoomId;
    private String useYn;
    private String createDt;
    private String modifyDt;
}
