package com.noonoo.prjtbackend.analytics.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "visitor_daily_unique")
@IdClass(VisitorDailyUnique.Pk.class)
public class VisitorDailyUnique {

    @Id
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Id
    @Column(name = "visitor_key", length = 128, nullable = false)
    private String visitorKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Pk implements Serializable {
        private LocalDate visitDate;
        private String visitorKey;
    }
}
