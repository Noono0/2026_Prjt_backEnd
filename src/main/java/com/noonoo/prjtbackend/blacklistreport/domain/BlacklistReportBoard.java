package com.noonoo.prjtbackend.blacklistreport.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 블랙리스트 제보 게시판 (DDL용 엔티티, 조회는 MyBatis)
 */
@Getter
@Setter
@Entity
@Table(name = "blacklist_report_board")
public class BlacklistReportBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blacklist_report_seq")
    private Long blacklistReportSeq;

    /** 제보대상 아이디(엑셀·필터 기준) */
    @Column(name = "blacklist_target_id", nullable = false, length = 200)
    private String blacklistTargetId;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "writer_member_seq")
    private Long writerMemberSeq;

    @Column(name = "writer_name", length = 100)
    private String writerName;

    @Column(name = "category_code", length = 50)
    private String categoryCode;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "dislike_count")
    private Long dislikeCount;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "report_count")
    private Long reportCount;

    @Column(name = "comment_allowed_yn", length = 1)
    private String commentAllowedYn;

    @Column(name = "reply_allowed_yn", length = 1)
    private String replyAllowedYn;

    @Column(name = "use_yn", length = 1)
    private String useYn;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "create_id", length = 50)
    private String createId;

    @Column(name = "create_ip", length = 45)
    private String createIp;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Column(name = "modify_id", length = 50)
    private String modifyId;

    @Column(name = "modify_ip", length = 45)
    private String modifyIp;
}
