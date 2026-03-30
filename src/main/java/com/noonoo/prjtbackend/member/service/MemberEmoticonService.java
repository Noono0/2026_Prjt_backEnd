package com.noonoo.prjtbackend.member.service;

import com.noonoo.prjtbackend.member.dto.MemberEmoticonDto;
import com.noonoo.prjtbackend.member.dto.MemberEmoticonSaveRequest;

import java.util.List;

public interface MemberEmoticonService {

    List<MemberEmoticonDto> findMyEmoticons();

    int addMyEmoticon(MemberEmoticonSaveRequest request);

    int deleteMyEmoticon(Long memberEmoticonSeq);
}
