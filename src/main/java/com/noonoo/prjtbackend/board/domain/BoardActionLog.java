package com.noonoo.prjtbackend.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "board_action_log")
public class BoardActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_action_log_seq")
    private Long boardActionLogSeq;

    @Column(name = "board_kind", length = 30, nullable = false)
    private String boardKind;

    @Column(name = "target_kind", length = 20, nullable = false)
    private String targetKind;

    @Column(name = "target_seq", nullable = false)
    private Long targetSeq;

    @Column(name = "action_type", length = 20, nullable = false)
    private String actionType;

    @Column(name = "member_seq")
    private Long memberSeq;

    @Column(name = "member_id", length = 100)
    private String memberId;

    @Column(name = "client_ip", length = 45)
    private String clientIp;

    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;
}
