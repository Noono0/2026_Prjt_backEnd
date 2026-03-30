package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.board.dto.BoardDto;
import com.noonoo.prjtbackend.member.dto.PointPolicyRowDto;
import com.noonoo.prjtbackend.member.mapper.PointPolicyMapper;
import com.noonoo.prjtbackend.member.service.PointPolicyResolver;
import com.noonoo.prjtbackend.member.service.PointPolicyService;
import com.noonoo.prjtbackend.member.service.WalletPointGrantService;
import com.noonoo.prjtbackend.member.wallet.PointPolicyKeys;
import com.noonoo.prjtbackend.member.wallet.WalletPointRules;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {

    private static final List<String> POLICY_ORDER = List.of(
            PointPolicyKeys.SIGNUP,
            PointPolicyKeys.FREE_BOARD_POST,
            PointPolicyKeys.BOARD_COMMENT_FIRST,
            PointPolicyKeys.BOARD_COMMENT_EXTRA,
            PointPolicyKeys.NOTICE_COMMENT_FIRST,
            PointPolicyKeys.NOTICE_COMMENT_EXTRA,
            PointPolicyKeys.FREE_BOARD_LIKE);

    private final PointPolicyMapper pointPolicyMapper;
    private final PointPolicyResolver pointPolicyResolver;
    private final WalletPointGrantService walletPointGrantService;

    @Override
    public List<PointPolicyRowDto> listAllPolicies() {
        List<PointPolicyRowDto> db = pointPolicyMapper.selectAllPolicies();
        Map<String, PointPolicyRowDto> map =
                db.stream().collect(Collectors.toMap(PointPolicyRowDto::getPolicyKey, r -> r, (a, b) -> a, LinkedHashMap::new));
        List<PointPolicyRowDto> out = new ArrayList<>();
        for (String key : POLICY_ORDER) {
            out.add(map.containsKey(key) ? map.get(key) : defaultRow(key));
        }
        return out;
    }

    private static PointPolicyRowDto defaultRow(String key) {
        PointPolicyRowDto d = new PointPolicyRowDto();
        d.setPolicyKey(key);
        d.setUseYn("Y");
        d.setThresholdInt(null);
        d.setRewardPoints(null);
        d.setCapInt(null);
        switch (key) {
            case PointPolicyKeys.SIGNUP -> d.setRewardPoints(WalletPointRules.SIGNUP_BONUS);
            case PointPolicyKeys.FREE_BOARD_POST -> d.setRewardPoints(WalletPointRules.FREE_BOARD_POST);
            case PointPolicyKeys.BOARD_COMMENT_FIRST -> d.setRewardPoints(WalletPointRules.COMMENT_FIRST_ON_POST);
            case PointPolicyKeys.BOARD_COMMENT_EXTRA -> d.setCapInt((int) WalletPointRules.COMMENT_EXTRA_CAP_PER_POST);
            case PointPolicyKeys.NOTICE_COMMENT_FIRST -> d.setRewardPoints(WalletPointRules.COMMENT_FIRST_ON_POST);
            case PointPolicyKeys.NOTICE_COMMENT_EXTRA -> d.setCapInt((int) WalletPointRules.COMMENT_EXTRA_CAP_PER_POST);
            case PointPolicyKeys.FREE_BOARD_LIKE -> {
                d.setThresholdInt(50);
                d.setRewardPoints(100L);
            }
            default -> {
            }
        }
        return d;
    }

    @Override
    @Transactional
    public void saveAllPolicies(List<PointPolicyRowDto> rows) {
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("저장할 정책이 없습니다.");
        }
        for (PointPolicyRowDto row : rows) {
            if (row == null || !StringUtils.hasText(row.getPolicyKey())) {
                throw new IllegalArgumentException("policyKey이 없습니다.");
            }
            String u = row.getUseYn() != null ? row.getUseYn().trim().toUpperCase() : "Y";
            row.setUseYn("Y".equals(u) ? "Y" : "N");

            String k = row.getPolicyKey().trim();
            if ("Y".equals(row.getUseYn())) {
                switch (k) {
                    case PointPolicyKeys.FREE_BOARD_LIKE -> {
                        if (row.getThresholdInt() == null || row.getThresholdInt() < 1) {
                            throw new IllegalArgumentException("자유게시판 추천 마일스톤: 추천 수 임계값은 1 이상이어야 합니다.");
                        }
                        if (row.getRewardPoints() == null || row.getRewardPoints() < 1) {
                            throw new IllegalArgumentException("자유게시판 추천 마일스톤: 보상 포인트는 1 이상이어야 합니다.");
                        }
                    }
                    case PointPolicyKeys.BOARD_COMMENT_EXTRA, PointPolicyKeys.NOTICE_COMMENT_EXTRA -> {
                        if (row.getCapInt() == null || row.getCapInt() < 0) {
                            throw new IllegalArgumentException(k + ": 게시글당 추가 적립 상한(cap)은 0 이상이어야 합니다.");
                        }
                    }
                    default -> {
                        if (row.getRewardPoints() == null || row.getRewardPoints() < 1) {
                            throw new IllegalArgumentException(k + ": 보상 포인트는 1 이상이어야 합니다.");
                        }
                    }
                }
            }
            int n = pointPolicyMapper.updatePolicy(row);
            if (n <= 0) {
                throw new IllegalStateException("정책 행이 없습니다: " + k + " (DB 시드·마이그레이션을 확인하세요.)");
            }
        }
    }

    @Override
    @Transactional
    public void tryGrantFreeBoardLikeMilestone(BoardDto boardAfterLike) {
        if (boardAfterLike == null || boardAfterLike.getBoardSeq() == null) {
            return;
        }
        Long author = boardAfterLike.getWriterMemberSeq();
        if (author == null || author <= 0) {
            return;
        }
        int th = pointPolicyResolver.threshold(PointPolicyKeys.FREE_BOARD_LIKE, 50);
        long reward = pointPolicyResolver.reward(PointPolicyKeys.FREE_BOARD_LIKE, 100L);
        if (th <= 0 || reward <= 0) {
            return;
        }
        long likes = boardAfterLike.getLikeCount() != null ? boardAfterLike.getLikeCount() : 0L;
        if (likes < th) {
            return;
        }
        int reserved = pointPolicyMapper.insertMilestoneGrantedIfAbsent(
                boardAfterLike.getBoardSeq(), author, reward);
        if (reserved <= 0) {
            return;
        }
        boolean ok = walletPointGrantService.creditPoints(
                author,
                WalletPointRules.REASON_FREE_BOARD_LIKE_MILESTONE,
                String.format("자유게시판 글 추천 %d개 달성 보상 (boardSeq=%d)", th, boardAfterLike.getBoardSeq()),
                reward);
        if (!ok) {
            pointPolicyMapper.deleteMilestoneGranted(boardAfterLike.getBoardSeq());
            log.warn("추천 마일스톤 포인트 지급 실패 후 예약 행 삭제 boardSeq={}", boardAfterLike.getBoardSeq());
        }
    }
}
