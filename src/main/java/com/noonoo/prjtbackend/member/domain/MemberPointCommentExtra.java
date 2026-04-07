package com.noonoo.prjtbackend.member.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "member_point_comment_extra")
@IdClass(MemberPointCommentExtra.Pk.class)
public class MemberPointCommentExtra {

    @Id
    @Column(name = "member_seq", nullable = false)
    private Long memberSeq;

    @Id
    @Column(name = "post_type", length = 16, nullable = false)
    private String postType;

    @Id
    @Column(name = "post_seq", nullable = false)
    private Long postSeq;

    @Column(name = "extra_points_earned", nullable = false)
    private Integer extraPointsEarned;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Pk implements Serializable {
        private Long memberSeq;
        private String postType;
        private Long postSeq;
    }
}
