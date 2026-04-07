package com.noonoo.prjtbackend.noticeBoard.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notice_board_comment_vote")
@IdClass(NoticeBoardCommentVote.Pk.class)
public class NoticeBoardCommentVote {

    @Id
    @Column(name = "notice_board_comment_seq", nullable = false)
    private Long noticeBoardCommentSeq;

    @Id
    @Column(name = "member_seq", nullable = false)
    private Long memberSeq;

    @Column(name = "vote_type", length = 1, nullable = false)
    private String voteType;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Pk implements Serializable {
        private Long noticeBoardCommentSeq;
        private Long memberSeq;
    }
}
