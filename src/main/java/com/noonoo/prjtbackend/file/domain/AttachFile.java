package com.noonoo.prjtbackend.file.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "attach_file")
public class AttachFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_seq")
    private Long fileSeq;

    @Column(name = "original_name", length = 500, nullable = false)
    private String originalName;

    @Column(name = "stored_path", length = 1000, nullable = false)
    private String storedPath;

    @Column(name = "content_type", length = 200)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "menu_url", length = 500)
    private String menuUrl;

    @Column(name = "member_seq")
    private Long memberSeq;

    @Column(name = "create_id", length = 50)
    private String createId;

    @Column(name = "create_ip", length = 45)
    private String createIp;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn;
}
