package com.noonoo.prjtbackend.eventbattle.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "event_battle_option")
public class EventBattleOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_battle_option_seq")
    private Long eventBattleOptionSeq;

    @Column(name = "event_battle_seq", nullable = false)
    private Long eventBattleSeq;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "label", length = 200, nullable = false)
    private String label;

    @Column(name = "points_total", nullable = false)
    private Long pointsTotal;
}
