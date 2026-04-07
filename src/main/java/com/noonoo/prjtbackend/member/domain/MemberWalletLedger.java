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
@Table(name = "member_wallet_ledger")
public class MemberWalletLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_seq")
    private Long ledgerSeq;

    @Column(name = "member_seq", nullable = false)
    private Long memberSeq;

    @Column(name = "reason_code", length = 40, nullable = false)
    private String reasonCode;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "point_delta", nullable = false)
    private Long pointDelta;

    @Column(name = "iron_delta", nullable = false)
    private Integer ironDelta;

    @Column(name = "silver_delta", nullable = false)
    private Integer silverDelta;

    @Column(name = "gold_delta", nullable = false)
    private Integer goldDelta;

    @Column(name = "diamond_delta", nullable = false)
    private Integer diamondDelta;

    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "create_id", length = 50)
    private String createId;
}
