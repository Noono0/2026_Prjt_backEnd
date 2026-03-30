package com.noonoo.prjtbackend.member.service;

import com.noonoo.prjtbackend.member.dto.PointRankingEntryDto;

import java.util.List;

public interface PointRankingService {

    List<PointRankingEntryDto> ranking(String period);
}
