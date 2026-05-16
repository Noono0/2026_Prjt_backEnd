package com.noonoo.prjtbackend.gamniverseprofile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoopLiveStatusItemDto {

    private boolean isLive;
    private String liveRoomId;
}
