package com.noonoo.prjtbackend.member.mapper;

import com.noonoo.prjtbackend.member.dto.MemberWalletBalance;
import com.noonoo.prjtbackend.member.dto.MemberWalletLedgerDto;
import com.noonoo.prjtbackend.member.dto.PointRankingBreakdownRowDto;
import com.noonoo.prjtbackend.member.dto.PointRankingRowDto;
import com.noonoo.prjtbackend.member.dto.WalletLedgerSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberWalletMapper {

    MemberWalletBalance selectWalletByMemberSeq(@Param("memberSeq") long memberSeq);

    int insertWallet(@Param("memberSeq") long memberSeq);

    int addPoints(@Param("memberSeq") long memberSeq, @Param("delta") long delta);

    /** 잔액이 delta 이상일 때만 차감 */
    int subtractPointsIfEnough(@Param("memberSeq") long memberSeq, @Param("delta") long delta);

    int purchaseIron(
            @Param("memberSeq") long memberSeq,
            @Param("cost") long cost,
            @Param("qty") int qty);

    int exchangeIronToSilver(
            @Param("memberSeq") long memberSeq,
            @Param("ironCost") int ironCost,
            @Param("silverGain") int silverGain);

    int exchangeSilverToGold(
            @Param("memberSeq") long memberSeq,
            @Param("silverCost") int silverCost,
            @Param("goldGain") int goldGain);

    int exchangeGoldToDiamond(
            @Param("memberSeq") long memberSeq,
            @Param("goldCost") int goldCost,
            @Param("diamondGain") int diamondGain);

    int insertLedger(MemberWalletLedgerDto row);

    long countLedger(@Param("memberSeq") long memberSeq);

    List<MemberWalletLedgerDto> selectLedgerPage(WalletLedgerSearchCondition condition);

    List<PointRankingRowDto> selectPointRanking(
            @Param("fromInclusive") String fromInclusive,
            @Param("toExclusive") String toExclusive);

    List<PointRankingBreakdownRowDto> selectPointRankingBreakdown(
            @Param("fromInclusive") String fromInclusive,
            @Param("toExclusive") String toExclusive,
            @Param("memberSeqs") List<Long> memberSeqs);
}
