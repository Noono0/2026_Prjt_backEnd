package com.noonoo.prjtbackend.blacklistreport.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "blacklist_report_comment_report")
@IdClass(BlacklistReportCommentReport.Pk.class)
public class BlacklistReportCommentReport {

    @Id
    @Column(name = "blacklist_report_comment_seq", nullable = false)
    private Long blacklistReportCommentSeq;

    @Id
    @Column(name = "member_seq", nullable = false)
    private Long memberSeq;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Pk implements Serializable {
        private Long blacklistReportCommentSeq;
        private Long memberSeq;
    }
}
