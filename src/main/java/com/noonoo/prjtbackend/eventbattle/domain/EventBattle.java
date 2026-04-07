package com.noonoo.prjtbackend.eventbattle.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "event_battle")
public class EventBattle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_battle_seq")
    private Long eventBattleSeq;

    @Column(name = "title", length = 500, nullable = false)
    private String title;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "vote_limit_per_member", nullable = false)
    private Integer voteLimitPerMember;

    @Column(name = "vote_only_yn", length = 1, nullable = false)
    private String voteOnlyYn;

    @Column(name = "winner_option_seq")
    private Long winnerOptionSeq;

    @Column(name = "creator_member_seq", nullable = false)
    private Long creatorMemberSeq;

    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn;

    @Column(name = "create_id", length = 50)
    private String createId;

    @Column(name = "create_ip", length = 45)
    private String createIp;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "modify_id", length = 50)
    private String modifyId;

    @Column(name = "modify_ip", length = 45)
    private String modifyIp;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Column(name = "settle_dt")
    private LocalDateTime settleDt;
}
