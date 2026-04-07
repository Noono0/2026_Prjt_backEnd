package com.noonoo.prjtbackend.contentfilter.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "content_filter_word")
public class ContentFilterWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_filter_word_seq")
    private Long contentFilterWordSeq;

    @Column(name = "category", length = 20, nullable = false)
    private String category;

    @Column(name = "keyword", length = 200, nullable = false)
    private String keyword;

    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;
}
