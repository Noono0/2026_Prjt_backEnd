package com.noonoo.prjtbackend.blacklistreport.serviceImpl;

import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistPopularLikeRuleRow;
import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportDto;
import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportSaveRequest;
import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportSearchCondition;
import com.noonoo.prjtbackend.blacklistreport.mapper.BlacklistReportMapper;
import com.noonoo.prjtbackend.blacklistreport.service.BlacklistReportService;
import com.noonoo.prjtbackend.blacklistreport.support.BlacklistReportExcelExporter;
import com.noonoo.prjtbackend.board.support.BoardBlindSupport;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.common.security.AuthenticatedMember;
import com.noonoo.prjtbackend.common.security.CurrentMemberService;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterApplyService;
import com.noonoo.prjtbackend.member.MemberDisplayNames;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlacklistReportServiceImpl implements BlacklistReportService {

    private final BlacklistReportMapper blacklistReportMapper;
    private final MemberMapper memberMapper;
    private final ContentFilterApplyService contentFilterApplyService;
    private final CurrentMemberService currentMemberService;
    private final BoardBlindSupport boardBlindSupport;

    @Override
    public List<OptionDto> findBlacklistCategoryOptions() {
        return blacklistReportMapper.findBlacklistCategoryOptions();
    }

    @Override
    public List<OptionDto> findBlacklistListCategoryOptions() {
        return blacklistReportMapper.findBlacklistListCategoryOptions();
    }

    /**
     * 요청 categoryCode가 목록 조회 그룹(A0006) 내 A00052/A00053 행의 code_value 또는 code_name과 같으면
     * 카테고리 필터 대신 attr1 추천 수 이상만 조회한다.
     */
    private void applyPopularCategoryAsMinLikes(BlacklistReportSearchCondition c) {
        if (c == null || !StringUtils.hasText(c.getCategoryCode())) {
            return;
        }
        List<BlacklistPopularLikeRuleRow> rows = blacklistReportMapper.findBlacklistPopularLikeRules();
        if (rows == null || rows.isEmpty()) {
            return;
        }
        String req = c.getCategoryCode().trim();
        for (BlacklistPopularLikeRuleRow row : rows) {
            boolean matchValue =
                    StringUtils.hasText(row.getCodeValue()) && req.equals(row.getCodeValue().trim());
            boolean matchName =
                    StringUtils.hasText(row.getCodeName()) && req.equals(row.getCodeName().trim());
            if (matchValue || matchName) {
                c.setCategoryCode(null);
                c.setMinLikeCount(resolvePopularThreshold(row));
                return;
            }
        }
    }

    private int resolvePopularThreshold(BlacklistPopularLikeRuleRow raw) {
        if (raw == null) {
            return 50;
        }
        Integer fromAttr = parseNonNegativeIntLoose(raw.getAttr1());
        if (fromAttr != null) {
            return fromAttr;
        }
        return 50;
    }

    private static Integer parseNonNegativeIntLoose(Object attr1) {
        if (attr1 == null) {
            return null;
        }
        String s = attr1.toString().trim();
        if (!StringUtils.hasText(s)) {
            return null;
        }
        try {
            double d = Double.parseDouble(s);
            int v = (int) Math.floor(d);
            if (v >= 0) {
                return v;
            }
        } catch (NumberFormatException ignored) {
            // fall through
        }
        return null;
    }

    @Override
    public PageResponse<BlacklistReportDto> findBlacklistReports(BlacklistReportSearchCondition condition) {
        boardBlindSupport.applyBlacklistReportBlindParams(condition);
        applyPopularCategoryAsMinLikes(condition);
        long total = blacklistReportMapper.findBlacklistReportsCnt(condition);
        List<BlacklistReportDto> items = blacklistReportMapper.findBlacklistReports(condition);
        return PagingUtils.toPageResponse(condition, total, items);
    }

    @Override
    public BlacklistReportDto findDetail(Long blacklistReportSeq) {
        BlacklistReportDto b = blacklistReportMapper.findById(blacklistReportSeq);
        if (b == null) {
            return null;
        }
        if (boardBlindSupport.isBlacklistReportBlind(b)) {
            return null;
        }
        return b;
    }

    @Override
    @Transactional
    public int create(BlacklistReportSaveRequest req) {
        if (!StringUtils.hasText(req.getBlacklistTargetId()) || !StringUtils.hasText(req.getTitle())) {
            throw new IllegalArgumentException("블랙리스트 아이디와 제목은 필수입니다.");
        }
        String clientIp = RequestContext.getClientIp();
        Optional<AuthenticatedMember> auth = currentMemberService.resolve();
        Long loginMemberSeq = auth.map(AuthenticatedMember::memberSeq).orElse(null);
        String loginMemberId = auth.map(AuthenticatedMember::memberId).orElse(null);

        String writerName = null;
        if (StringUtils.hasText(loginMemberId)) {
            MemberDto loginMember = memberMapper.findLoginMember(loginMemberId);
            if (loginMember != null) {
                writerName = MemberDisplayNames.fromMember(loginMember);
            }
        }

        req.setWriterMemberSeq(loginMemberSeq);
        req.setWriterName(StringUtils.hasText(writerName) ? writerName : loginMemberId);
        req.setCreateId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        req.setModifyId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        req.setCreateIp(clientIp);
        req.setModifyIp(clientIp);
        if (req.getViewCount() == null) {
            req.setViewCount(0L);
        }
        if (req.getLikeCount() == null) {
            req.setLikeCount(0L);
        }
        if (req.getDislikeCount() == null) {
            req.setDislikeCount(0L);
        }
        if (req.getCommentCount() == null) {
            req.setCommentCount(0L);
        }
        if (req.getReportCount() == null) {
            req.setReportCount(0L);
        }
        if (!StringUtils.hasText(req.getCommentAllowedYn())) {
            req.setCommentAllowedYn("Y");
        }
        if (!StringUtils.hasText(req.getReplyAllowedYn())) {
            req.setReplyAllowedYn("Y");
        }

        req.setBlacklistTargetId(req.getBlacklistTargetId().trim());
        req.setTitle(contentFilterApplyService.applyField("제목", req.getTitle()));
        req.setContent(contentFilterApplyService.applyField("내용", req.getContent()));

        int n = blacklistReportMapper.insertBlacklistReport(req);
        if (n > 0 && req.getBlacklistReportSeq() == null) {
            long id = blacklistReportMapper.selectLastInsertId();
            if (id > 0) {
                req.setBlacklistReportSeq(id);
            }
        }
        return n;
    }

    @Override
    @Transactional
    public int update(BlacklistReportSaveRequest req) {
        if (req.getBlacklistReportSeq() == null || req.getBlacklistReportSeq() <= 0) {
            return 0;
        }
        Optional<AuthenticatedMember> auth = currentMemberService.resolve();
        if (auth.isEmpty()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        BlacklistReportDto existing = blacklistReportMapper.findById(req.getBlacklistReportSeq());
        if (existing == null) {
            return 0;
        }
        long me = auth.get().memberSeq();
        if (existing.getWriterMemberSeq() == null || !Objects.equals(existing.getWriterMemberSeq(), me)) {
            throw new IllegalArgumentException("본인이 작성한 글만 수정할 수 있습니다.");
        }
        String clientIp = RequestContext.getClientIp();
        String loginMemberId = auth.get().memberId();
        req.setModifyId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        req.setModifyIp(clientIp);
        if (!StringUtils.hasText(req.getBlacklistTargetId())) {
            throw new IllegalArgumentException("블랙리스트 아이디는 필수입니다.");
        }
        req.setBlacklistTargetId(req.getBlacklistTargetId().trim());
        req.setTitle(contentFilterApplyService.applyField("제목", req.getTitle()));
        req.setContent(contentFilterApplyService.applyField("내용", req.getContent()));
        if (!StringUtils.hasText(req.getCommentAllowedYn())) {
            req.setCommentAllowedYn("Y");
        }
        if (!StringUtils.hasText(req.getReplyAllowedYn())) {
            req.setReplyAllowedYn("Y");
        }
        return blacklistReportMapper.updateBlacklistReport(req);
    }

    @Override
    @Transactional
    public int deleteIfWriter(Long blacklistReportSeq) {
        if (blacklistReportSeq == null || blacklistReportSeq <= 0) {
            return 0;
        }
        Optional<AuthenticatedMember> auth = currentMemberService.resolve();
        if (auth.isEmpty()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        BlacklistReportDto b = blacklistReportMapper.findById(blacklistReportSeq);
        if (b == null) {
            return 0;
        }
        long me = auth.get().memberSeq();
        if (b.getWriterMemberSeq() == null || !Objects.equals(b.getWriterMemberSeq(), me)) {
            throw new IllegalArgumentException("본인이 작성한 글만 삭제할 수 있습니다.");
        }
        return blacklistReportMapper.deleteBlacklistReport(blacklistReportSeq);
    }

    @Override
    @Transactional
    public int increaseViewCount(Long blacklistReportSeq) {
        BlacklistReportDto b = blacklistReportMapper.findById(blacklistReportSeq);
        if (b == null || boardBlindSupport.isBlacklistReportBlind(b)) {
            return 0;
        }
        return blacklistReportMapper.increaseViewCount(blacklistReportSeq);
    }

    @Override
    @Transactional
    public int likeBlacklistReport(Long blacklistReportSeq) {
        BlacklistReportDto b = blacklistReportMapper.findById(blacklistReportSeq);
        if (b == null || boardBlindSupport.isBlacklistReportBlind(b)) {
            return 0;
        }
        return increaseWithActionLog("BLACKLIST_REPORT", "POST", blacklistReportSeq, "LIKE", () ->
                blacklistReportMapper.increaseBlacklistReportLikeCount(blacklistReportSeq));
    }

    @Override
    @Transactional
    public int dislikeBlacklistReport(Long blacklistReportSeq) {
        BlacklistReportDto b = blacklistReportMapper.findById(blacklistReportSeq);
        if (b == null || boardBlindSupport.isBlacklistReportBlind(b)) {
            return 0;
        }
        return increaseWithActionLog("BLACKLIST_REPORT", "POST", blacklistReportSeq, "DISLIKE", () ->
                blacklistReportMapper.increaseBlacklistReportDislikeCount(blacklistReportSeq));
    }

    @Override
    @Transactional
    public int reportBlacklistReport(Long blacklistReportSeq) {
        BlacklistReportDto b = blacklistReportMapper.findById(blacklistReportSeq);
        if (b == null || boardBlindSupport.isBlacklistReportBlind(b)) {
            return 0;
        }
        return increaseWithActionLog("BLACKLIST_REPORT", "POST", blacklistReportSeq, "REPORT", () ->
                blacklistReportMapper.increaseBlacklistReportReportCount(blacklistReportSeq));
    }

    private int increaseWithActionLog(String boardKind,
                                      String targetKind,
                                      Long targetSeq,
                                      String actionType,
                                      CounterUpdater counterUpdater) {
        Long memberSeq = RequestContext.getLoginMemberSeq();
        String memberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        int inserted = blacklistReportMapper.insertBlacklistReportActionLog(
                boardKind,
                targetKind,
                targetSeq,
                actionType,
                memberSeq,
                memberId,
                clientIp
        );

        if (inserted <= 0) {
            return 0;
        }
        return counterUpdater.update();
    }

    @FunctionalInterface
    private interface CounterUpdater {
        int update();
    }

    @Override
    public byte[] exportExcel(
            String blacklistTargetId,
            String keyword,
            String createDtFrom,
            String createDtTo,
            String categoryCode,
            String columnsCsv
    ) throws Exception {
        BlacklistReportSearchCondition c = new BlacklistReportSearchCondition();
        if (StringUtils.hasText(blacklistTargetId)) {
            c.setBlacklistTargetId(blacklistTargetId.trim());
        }
        if (StringUtils.hasText(keyword)) {
            c.setKeyword(keyword.trim());
        }
        if (StringUtils.hasText(createDtFrom)) {
            c.setCreateDtFrom(createDtFrom.trim());
        }
        if (StringUtils.hasText(createDtTo)) {
            c.setCreateDtTo(createDtTo.trim());
        }
        if (StringUtils.hasText(categoryCode)) {
            c.setCategoryCode(categoryCode.trim());
        }
        List<BlacklistReportDto> rows = blacklistReportMapper.findBlacklistReportsForExport(c);
        return BlacklistReportExcelExporter.toXlsx(rows, BlacklistReportExcelExporter.parseKeysQuery(columnsCsv));
    }
}
