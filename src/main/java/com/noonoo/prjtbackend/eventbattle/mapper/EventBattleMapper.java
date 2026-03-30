package com.noonoo.prjtbackend.eventbattle.mapper;

import com.noonoo.prjtbackend.eventbattle.dto.EventBattleBettorRankDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleBetRowDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleMyBetDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleOptionDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSaveRequest;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSearchCondition;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleStakeMemberRow;
import com.noonoo.prjtbackend.eventbattle.dto.MemberStakeRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EventBattleMapper {

    int insertEvent(EventBattleSaveRequest row);

    int insertOption(
            @Param("eventBattleSeq") long eventBattleSeq,
            @Param("sortOrder") int sortOrder,
            @Param("label") String label
    );

    EventBattleDto selectById(@Param("eventBattleSeq") long eventBattleSeq);

    List<EventBattleOptionDto> selectOptionsByEventSeq(@Param("eventBattleSeq") long eventBattleSeq);

    EventBattleOptionDto selectOptionById(@Param("eventBattleOptionSeq") long eventBattleOptionSeq);

    List<EventBattleDto> findEvents(EventBattleSearchCondition condition);

    long findEventsCnt(EventBattleSearchCondition condition);

    int incrementOptionPoints(
            @Param("eventBattleOptionSeq") long eventBattleOptionSeq,
            @Param("delta") long delta
    );

    int insertBet(
            @Param("eventBattleSeq") long eventBattleSeq,
            @Param("eventBattleOptionSeq") long eventBattleOptionSeq,
            @Param("memberSeq") long memberSeq,
            @Param("pointAmount") long pointAmount
    );

    EventBattleMyBetDto selectMyBet(
            @Param("eventBattleSeq") long eventBattleSeq,
            @Param("memberSeq") long memberSeq
    );

    long countBets(@Param("eventBattleSeq") long eventBattleSeq);

    long countDistinctBetMembers(@Param("eventBattleSeq") long eventBattleSeq);

    Long maxBetSeq(@Param("eventBattleSeq") long eventBattleSeq);

    List<EventBattleBetRowDto> selectRecentBets(
            @Param("eventBattleSeq") long eventBattleSeq,
            @Param("sinceSeq") Long sinceSeq,
            @Param("limit") int limit
    );

    List<EventBattleBetRowDto> selectRecentBetsBefore(
            @Param("eventBattleSeq") long eventBattleSeq,
            @Param("beforeBetSeq") long beforeBetSeq,
            @Param("limit") int limit
    );

    List<EventBattleBetRowDto> selectMyBetsForEvent(
            @Param("eventBattleSeq") long eventBattleSeq,
            @Param("memberSeq") long memberSeq,
            @Param("limit") int limit
    );

    List<EventBattleBettorRankDto> selectBettorRanking(
            @Param("eventBattleSeq") long eventBattleSeq,
            @Param("limit") int limit
    );

    List<MemberStakeRow> sumStakesByOption(
            @Param("eventBattleSeq") long eventBattleSeq,
            @Param("eventBattleOptionSeq") long eventBattleOptionSeq
    );

    /** 승리 옵션별 회원 베팅 합 + 닉네임 (정산 지급 시뮬레이션용, member_seq 오름차순) */
    List<EventBattleStakeMemberRow> selectStakesByOptionWithMember(
            @Param("eventBattleSeq") long eventBattleSeq,
            @Param("eventBattleOptionSeq") long eventBattleOptionSeq
    );

    List<MemberStakeRow> sumStakesAllMembers(@Param("eventBattleSeq") long eventBattleSeq);

    int updateSettled(
            @Param("eventBattleSeq") long eventBattleSeq,
            @Param("winnerOptionSeq") Long winnerOptionSeq,
            @Param("modifyId") String modifyId,
            @Param("modifyIp") String modifyIp
    );
}
