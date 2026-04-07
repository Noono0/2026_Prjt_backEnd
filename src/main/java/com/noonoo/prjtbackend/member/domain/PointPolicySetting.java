package com.noonoo.prjtbackend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "point_policy_setting")
public class PointPolicySetting {

    @Id
    @Column(name = "policy_key", length = 64, nullable = false)
    private String policyKey;

    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn;

    @Column(name = "threshold_int")
    private Integer thresholdInt;

    @Column(name = "reward_points")
    private Long rewardPoints;

    @Column(name = "cap_int")
    private Integer capInt;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;
}
