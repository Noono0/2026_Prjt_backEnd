package com.noonoo.prjtbackend.noticeBoard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "notice_board")
public class NoticeBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_board_seq")
    private Long noticeBoardSeq;

    @Column(name = "category_code")
    private String categoryCode;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "writer_member_seq")
    private Long writerMemberSeq;

    @Column(name = "writer_name")
    private String writerName;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "dislike_count")
    private Long dislikeCount;

    @Column(name = "report_count")
    private Long reportCount;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "comment_like_count")
    private Long commentLikeCount;

    @Column(name = "comment_report_count")
    private Long commentReportCount;

    @Column(name = "show_yn")
    private String showYn;

    @Column(name = "highlight_yn")
    private String highlightYn;

    @Column(name = "comment_allowed_yn")
    private String commentAllowedYn;

    @Column(name = "reply_allowed_yn")
    private String replyAllowedYn;

    /** 자유게시판 상단 고정 공지 */
    @Column(name = "pin_on_free_board_yn")
    private String pinOnFreeBoardYn;

    /** 행(소프트) 삭제 여부 — MyBatis 공지 API와 동일 */
    @Column(name = "use_yn")
    private String useYn;

    /** 사이트 로드 시 공지 팝업(레거시·DB 이관용 컬럼 — data.sql / site_popup 이관과 동일 정의) */
    @Column(name = "popup_yn")
    private String popupYn;

    @Column(name = "popup_type")
    private String popupType;

    @Column(name = "popup_width")
    private Integer popupWidth;

    @Column(name = "popup_height")
    private Integer popupHeight;

    @Column(name = "popup_pos_x")
    private Integer popupPosX;

    @Column(name = "popup_pos_y")
    private Integer popupPosY;

    @Column(name = "popup_start_dt")
    private LocalDateTime popupStartDt;

    @Column(name = "popup_end_dt")
    private LocalDateTime popupEndDt;

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
