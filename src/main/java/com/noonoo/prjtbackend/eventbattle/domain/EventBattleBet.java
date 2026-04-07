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
@Table(name = "event_battle_bet")
public class EventBattleBet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_battle_bet_seq")
    private Long eventBattleBetSeq;

    @Column(name = "event_battle_seq", nullable = false)
    private Long eventBattleSeq;

    @Column(name = "event_battle_option_seq", nullable = false)
    private Long eventBattleOptionSeq;

    @Column(name = "member_seq", nullable = false)
    private Long memberSeq;

    @Column(name = "point_amount", nullable = false)
    private Long pointAmount;

    @Column(name = "create_dt")
    private LocalDateTime createDt;
}
