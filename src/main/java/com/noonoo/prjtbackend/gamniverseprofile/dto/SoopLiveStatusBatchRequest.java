package com.noonoo.prjtbackend.gamniverseprofile.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoopLiveStatusBatchRequest {

    /** 숲 방송 링크 (최대 {@code app.soop-live.max-batch-size}건) */
    private List<String> links;
}
