package com.noonoo.prjtbackend.analytics.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "visitor_heartbeat")
public class VisitorHeartbeat {

    @Id
    @Column(name = "visitor_key", length = 128, nullable = false)
    private String visitorKey;

    @Column(name = "member_seq")
    private Long memberSeq;

    @Column(name = "client_ip", length = 100)
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "first_seen_at", nullable = false)
    private LocalDateTime firstSeenAt;

    @Column(name = "last_seen_at", nullable = false)
    private LocalDateTime lastSeenAt;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;
}
