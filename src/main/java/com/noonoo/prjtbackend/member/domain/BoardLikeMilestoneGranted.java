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
@Table(name = "board_like_milestone_granted")
public class BoardLikeMilestoneGranted {

    @Id
    @Column(name = "board_seq", nullable = false)
    private Long boardSeq;

    @Column(name = "writer_member_seq", nullable = false)
    private Long writerMemberSeq;

    @Column(name = "reward_points", nullable = false)
    private Long rewardPoints;

    @Column(name = "granted_dt", nullable = false)
    private LocalDateTime grantedDt;
}
