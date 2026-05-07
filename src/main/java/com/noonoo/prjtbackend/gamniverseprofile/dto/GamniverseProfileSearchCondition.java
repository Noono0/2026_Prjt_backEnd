package com.noonoo.prjtbackend.gamniverseprofile.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class GamniverseProfileSearchCondition extends PageRequest {
    private String profileName;
    private String affiliationCode;

    public GamniverseProfileSearchCondition() {
        setSortBy("sortOrder");
        setSortDir("asc");
    }
}
