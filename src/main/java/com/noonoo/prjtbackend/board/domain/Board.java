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
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_seq")
    private Long boardSeq;

    @Column(name = "category_code")
    private String categoryCode;

    @Column(name = "title")
    private String title;

    /** 에디터 HTML 저장 */
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "writer_member_seq")
    private Long writerMemberSeq;

    @Column(name = "writer_name")
    private String writerName;

    @Column(name = "secret_yn")
    private String secretYn;

    @Column(name = "secret_password_hash")
    private String secretPasswordHash;

    @Column(name = "anonymous_yn")
    private String anonymousYn;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "dislike_count")
    private Long dislikeCount;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "comment_like_count")
    private Long commentLikeCount;

    @Column(name = "comment_report_count")
    private Long commentReportCount;

    @Column(name = "report_count")
    private Long reportCount;

    @Column(name = "show_yn")
    private String showYn;

    @Column(name = "highlight_yn")
    private String highlightYn;

    /** 행(소프트) 삭제 여부 — MyBatis 게시글 API와 동일 */
    @Column(name = "use_yn")
    private String useYn;

    @Column(name = "comment_allowed_yn")
    private String commentAllowedYn;

    @Column(name = "reply_allowed_yn")
    private String replyAllowedYn;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "create_id")
    private String createId;

    @Column(name = "create_ip")
    private String createIp;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Column(name = "modify_id")
    private String modifyId;

    @Column(name = "modify_ip")
    private String modifyIp;
}