package com.noonoo.prjtbackend.gamniverseprofile.dto;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 키: 정규화된 숲 방송 링크 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoopLiveStatusBatchResponse {

    private Map<String, SoopLiveStatusItemDto> statuses = new LinkedHashMap<>();
}
