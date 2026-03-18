package com.noonoo.prjtbackend.codeGroup.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "code_detail")
public class CodeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_detail_seq")
    private Long codeDetailSeq;

    @Column(name = "code_group_seq", nullable = false)
    private Long codeGroupSeq;

    @Column(name = "parent_detail_seq")
    private Long parentDetailSeq;

    @Column(name = "code_id", nullable = false, length = 100)
    private String codeId;

    @Column(name = "code_value", nullable = false, length = 100)
    private String codeValue;

    @Column(name = "code_name", nullable = false, length = 200)
    private String codeName;

    @Column(name = "code_level", nullable = false)
    private Integer codeLevel;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "use_yn", length = 1)
    private String useYn;

    @Column(name = "attr1", length = 200)
    private String attr1;

    @Column(name = "attr2", length = 200)
    private String attr2;

    @Column(name = "attr3", length = 200)
    private String attr3;

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

    @Column(name = "status", length = 20)
    private String status;
}