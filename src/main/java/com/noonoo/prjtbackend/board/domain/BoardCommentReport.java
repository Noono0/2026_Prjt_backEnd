package com.noonoo.prjtbackend.board.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "board_comment_report")
@IdClass(BoardCommentReport.Pk.class)
public class BoardCommentReport {

    @Id
    @Column(name = "board_comment_seq", nullable = false)
    private Long boardCommentSeq;

    @Id
    @Column(name = "member_seq", nullable = false)
    private Long memberSeq;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Pk implements Serializable {
        private Long boardCommentSeq;
        private Long memberSeq;
    }
}
