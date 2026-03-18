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
@Table(name = "code_group")
public class CodeGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_group_seq")
    private Long codeGroupSeq;

    @Column(name = "code_group_id", nullable = false, unique = true, length = 100)
    private String codeGroupId;

    @Column(name = "code_group_name", nullable = false, length = 200)
    private String codeGroupName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "use_yn", length = 1)
    private String useYn;

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