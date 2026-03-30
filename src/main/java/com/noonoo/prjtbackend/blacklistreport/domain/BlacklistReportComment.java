package com.noonoo.prjtbackend.blacklistreport.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 블랙리스트 제보 댓글 (DDL용, 조회·변경은 MyBatis)
 */
@Getter
@Setter
@Entity
@Table(name = "blacklist_report_comment")
public class BlacklistReportComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blacklist_report_comment_seq")
    private Long blacklistReportCommentSeq;

    @Column(name = "blacklist_report_seq", nullable = false)
    private Long blacklistReportSeq;

    @Column(name = "parent_blacklist_report_comment_seq")
    private Long parentBlacklistReportCommentSeq;

    @Column(name = "writer_member_seq")
    private Long writerMemberSeq;

    @Column(name = "writer_name", length = 100)
    private String writerName;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "emoticon_seq_1")
    private Long emoticonSeq1;

    @Column(name = "emoticon_seq_2")
    private Long emoticonSeq2;

    @Column(name = "emoticon_seq_3")
    private Long emoticonSeq3;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "dislike_count")
    private Long dislikeCount;

    @Column(name = "report_count")
    private Long reportCount;

    @Column(name = "show_yn", length = 1)
    private String showYn;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "create_id", length = 50)
    private String createId;

    @Column(name = "create_ip", length = 45)
    private String createIp;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;
}
