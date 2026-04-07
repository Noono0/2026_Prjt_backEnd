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
@Table(name = "notice_board_comment")
public class NoticeBoardComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_board_comment_seq")
    private Long noticeBoardCommentSeq;

    @Column(name = "notice_board_seq", nullable = false)
    private Long noticeBoardSeq;

    @Column(name = "parent_notice_board_comment_seq")
    private Long parentNoticeBoardCommentSeq;

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

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "dislike_count", nullable = false)
    private Long dislikeCount;

    @Column(name = "report_count", nullable = false)
    private Long reportCount;

    @Column(name = "show_yn", length = 1, nullable = false)
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
