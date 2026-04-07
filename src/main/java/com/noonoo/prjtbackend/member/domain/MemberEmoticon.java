package com.noonoo.prjtbackend.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "member_emoticon")
public class MemberEmoticon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_emoticon_seq")
    private Long memberEmoticonSeq;

    @Column(name = "member_seq", nullable = false)
    private Long memberSeq;

    @Column(name = "image_url", length = 512, nullable = false)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "create_dt")
    private LocalDateTime createDt;
}
