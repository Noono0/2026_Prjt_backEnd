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
@Table(name = "member_streamer_profile")
public class MemberStreamerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_streamer_profile_seq")
    private Long memberStreamerProfileSeq;

    @Column(name = "member_seq", nullable = false, unique = true)
    private Long memberSeq;

    @Column(name = "instagram_url", length = 500)
    private String instagramUrl;

    @Column(name = "youtube_url", length = 500)
    private String youtubeUrl;

    @Column(name = "soop_channel_url", length = 500)
    private String soopChannelUrl;

    @Column(name = "company_category_code", length = 100)
    private String companyCategoryCode;

    @Column(name = "blood_type", length = 10)
    private String bloodType;

    @Column(name = "career_history", columnDefinition = "LONGTEXT")
    private String careerHistory;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;
}
