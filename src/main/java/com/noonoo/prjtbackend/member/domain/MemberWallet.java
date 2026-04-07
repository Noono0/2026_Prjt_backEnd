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
@Table(name = "member_wallet")
public class MemberWallet {

    @Id
    @Column(name = "member_seq", nullable = false)
    private Long memberSeq;

    @Column(name = "point_balance", nullable = false)
    private Long pointBalance;

    @Column(name = "iron_qty", nullable = false)
    private Integer ironQty;

    @Column(name = "silver_qty", nullable = false)
    private Integer silverQty;

    @Column(name = "gold_qty", nullable = false)
    private Integer goldQty;

    @Column(name = "diamond_qty", nullable = false)
    private Integer diamondQty;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;
}
