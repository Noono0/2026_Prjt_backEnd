package com.noonoo.prjtbackend.board.serviceImpl;

import com.noonoo.prjtbackend.board.dto.BoardDto;
import com.noonoo.prjtbackend.board.dto.BoardPopularCodeRawDto;
import com.noonoo.prjtbackend.board.dto.BoardPopularConfigDto;
import com.noonoo.prjtbackend.board.dto.BoardSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardSearchCondition;
import com.noonoo.prjtbackend.board.mapper.BoardMapper;
import com.noonoo.prjtbackend.board.service.BoardService;
import com.noonoo.prjtbackend.board.support.BoardBlindSupport;
import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterApplyService;
import com.noonoo.prjtbackend.common.security.AuthenticatedMember;
import com.noonoo.prjtbackend.common.security.CurrentMemberService;
import com.noonoo.prjtbackend.member.MemberDisplayNames;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.service.PointPolicyService;
import com.noonoo.prjtbackend.member.service.WalletPointGrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;
    private final MemberMapper memberMapper;
    private final ContentFilterApplyService contentFilterApplyService;
    private final WalletPointGrantService walletPointGrantService;
    private final PointPolicyService pointPolicyService;
    private final CurrentMemberService currentMemberService;
    private final BoardBlindSupport boardBlindSupport;

    @Override
    public List<OptionDto> findBoardCategoryOptions() {
        return boardMapper.findBoardCategoryOptions();
    }

    @Override
    public BoardPopularConfigDto getBoardPopularConfig() {
        BoardPopularCodeRawDto raw = boardMapper.findBoardPopularCodeDetail();
        String badgeLabel = "인기글";
        if (raw != null && StringUtils.hasText(raw.getCodeName())) {
            badgeLabel = raw.getCodeName().trim();
        }
        return new BoardPopularConfigDto(resolvePopularThreshold(raw), badgeLabel);
    }

    /**
     * 인기글 탭: 요청 categoryCode가 공통코드(A0001·code_id A00017)의 code_value 또는 code_name과 같으면
     * 카테고리 필터 대신 추천 수 임계 이상만 조회한다.
     */
    private void applyPopularCategoryAsMinLikes(BoardSearchCondition c) {
        if (c == null || !StringUtils.hasText(c.getCategoryCode())) {
            return;
        }
        BoardPopularCodeRawDto row = boardMapper.findBoardPopularCodeDetail();
        if (row == null) {
            return;
        }
        String req = c.getCategoryCode().trim();
        boolean matchValue = StringUtils.hasText(row.getCodeValue()) && req.equals(row.getCodeValue().trim());
        boolean matchName = StringUtils.hasText(row.getCodeName()) && req.equals(row.getCodeName().trim());
        if (!matchValue && !matchName) {
            return;
        }
        c.setCategoryCode(null);
        c.setMinLikeCount(resolvePopularThreshold(row));
    }

    private int resolvePopularThreshold(BoardPopularCodeRawDto raw) {
        if (raw == null) {
            return 50;
        }
        Integer fromAttr = parseNonNegativeIntLoose(raw.getAttr1());
        if (fromAttr != null) {
            return fromAttr;
        }
        Integer parsed = parseFirstNonNegativeInt(raw.getCodeValue(), raw.getDescription());
        return parsed != null ? parsed : 50;
    }

    /** ATTR1 이 숫자/소수 문자열이면 파싱 (DB numeric 타입 매핑 대비) */
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

    private static Integer parseFirstNonNegativeInt(String... parts) {
        if (parts == null) {
            return null;
        }
        for (String s : parts) {
            if (!StringUtils.hasText(s)) {
                continue;
            }
            try {
                int v = Integer.parseInt(s.trim());
                if (v >= 0) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // 다음 필드 시도
            }
        }
        return null;
    }

    @Override
    public PageResponse<BoardDto> findBoards(BoardSearchCondition condition) {
        boardBlindSupport.applyBlindParams(condition);
        applyPopularCategoryAsMinLikes(condition);
        long totalCount = boardMapper.findBoardsCnt(condition);
        List<BoardDto> items = boardMapper.findBoards(condition);
        return PagingUtils.toPageResponse(condition, totalCount, items);
    }

    @Override
    public BoardDto findBoardDetail(Long boardSeq) {
        BoardDto b = boardMapper.findBoardById(boardSeq);
        if (b == null) {
            return null;
        }
        if (boardBlindSupport.isBlind(b)) {
            return null;
        }
        return b;
    }

    @Override
    @Transactional
    public int createBoard(BoardSaveRequest condition) {
        log.info("=======> /api/boards/createBoard serviceimpl param={}", condition);

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

        condition.setWriterMemberSeq(loginMemberSeq);
        condition.setWriterName(StringUtils.hasText(writerName) ? writerName : loginMemberId);
        condition.setCreateId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        condition.setModifyId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        condition.setCreateIp(clientIp);
        condition.setModifyIp(clientIp);

        if (!StringUtils.hasText(condition.getShowYn())) {
            condition.setShowYn("Y");
        }
        if (!StringUtils.hasText(condition.getHighlightYn())) {
            condition.setHighlightYn("N");
        }

        if (condition.getViewCount() == null) condition.setViewCount(0L);
        if (condition.getLikeCount() == null) condition.setLikeCount(0L);
        if (condition.getDislikeCount() == null) condition.setDislikeCount(0L);
        if (condition.getCommentCount() == null) condition.setCommentCount(0L);
        if (condition.getCommentLikeCount() == null) condition.setCommentLikeCount(0L);
        if (condition.getCommentReportCount() == null) condition.setCommentReportCount(0L);
        if (condition.getReportCount() == null) condition.setReportCount(0L);

        normalizeCommentReplyFlags(condition);
        condition.setTitle(contentFilterApplyService.applyField("제목", condition.getTitle()));
        condition.setContent(contentFilterApplyService.applyField("내용", condition.getContent()));

        int inserted = boardMapper.insertBoard(condition);
        if (inserted > 0) {
            if (condition.getBoardSeq() == null) {
                long id = boardMapper.selectLastInsertId();
                if (id > 0) {
                    condition.setBoardSeq(id);
                } else {
                    log.warn("board insert 후 board_seq 미부여 (LAST_INSERT_ID=0)");
                }
            }
            Long writerSeq = condition.getWriterMemberSeq();
            Long boardSeq = condition.getBoardSeq();
            if (writerSeq != null && writerSeq > 0 && boardSeq != null && boardSeq > 0) {
                try {
                    walletPointGrantService.grantFreeBoardPost(writerSeq, boardSeq, condition.getCategoryCode());
                } catch (Exception e) {
                    log.warn("자유게시판 글 작성 포인트 지급 실패 boardSeq={}: {}", boardSeq, e.toString());
                }
            } else {
                log.warn(
                        "자유게시판 글 작성 포인트 생략(작성자·글번호 없음) writerSeq={} boardSeq={} categoryCode={}",
                        writerSeq,
                        boardSeq,
                        condition.getCategoryCode());
            }
        }
        return inserted;
    }

    @Override
    @Transactional
    public int updateBoard(BoardSaveRequest condition) {
        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        condition.setModifyId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        condition.setModifyIp(clientIp);

        normalizeCommentReplyFlags(condition);
        condition.setTitle(contentFilterApplyService.applyField("제목", condition.getTitle()));
        condition.setContent(contentFilterApplyService.applyField("내용", condition.getContent()));

        BoardDto existing = boardMapper.findBoardById(condition.getBoardSeq());
        if (existing == null) {
            return 0;
        }
        if (boardBlindSupport.isBlind(existing)) {
            return 0;
        }

        return boardMapper.updateBoard(condition);
    }

    /** 댓글 비허용이면 답글도 N으로 맞춤. 값은 Y/N만 사용. */
    private void normalizeCommentReplyFlags(BoardSaveRequest condition) {
        if (!StringUtils.hasText(condition.getCommentAllowedYn())) {
            condition.setCommentAllowedYn("Y");
        } else {
            String c = condition.getCommentAllowedYn().trim().toUpperCase(Locale.ROOT);
            condition.setCommentAllowedYn("Y".equals(c) ? "Y" : "N");
        }
        if (!StringUtils.hasText(condition.getReplyAllowedYn())) {
            condition.setReplyAllowedYn("Y");
        } else {
            String r = condition.getReplyAllowedYn().trim().toUpperCase(Locale.ROOT);
            condition.setReplyAllowedYn("Y".equals(r) ? "Y" : "N");
        }
        if ("N".equals(condition.getCommentAllowedYn())) {
            condition.setReplyAllowedYn("N");
        }
    }

    @Override
    @Transactional
    public int deleteBoard(Long boardSeq) {
        return boardMapper.deleteBoard(boardSeq);
    }

    @Override
    @Transactional
    public int deleteBoardIfWriter(Long boardSeq) {
        if (boardSeq == null || boardSeq <= 0) {
            return 0;
        }
        Optional<AuthenticatedMember> auth = currentMemberService.resolve();
        if (auth.isEmpty()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        BoardDto board = boardMapper.findBoardById(boardSeq);
        if (board == null) {
            return 0;
        }
        long me = auth.get().memberSeq();
        Long writer = board.getWriterMemberSeq();
        if (writer == null || !Objects.equals(writer, me)) {
            throw new IllegalArgumentException("본인이 작성한 글만 삭제할 수 있습니다.");
        }
        return boardMapper.deleteBoard(boardSeq);
    }

    @Override
    @Transactional
    public int increaseViewCount(Long boardSeq) {
        BoardDto b = boardMapper.findBoardById(boardSeq);
        if (b == null || boardBlindSupport.isBlind(b)) {
            return 0;
        }
        return boardMapper.increaseBoardViewCount(boardSeq);
    }

    @Override
    @Transactional
    public int likeBoard(Long boardSeq) {
        BoardDto b = boardMapper.findBoardById(boardSeq);
        if (b == null || boardBlindSupport.isBlind(b)) {
            return 0;
        }
        int n = increaseWithActionLog("BOARD", "POST", boardSeq, "LIKE", () -> boardMapper.increaseBoardLikeCount(boardSeq));
        if (n > 0) {
            BoardDto board = boardMapper.findBoardById(boardSeq);
            pointPolicyService.tryGrantFreeBoardLikeMilestone(board);
        }
        return n;
    }

    @Override
    @Transactional
    public int dislikeBoard(Long boardSeq) {
        BoardDto b = boardMapper.findBoardById(boardSeq);
        if (b == null || boardBlindSupport.isBlind(b)) {
            return 0;
        }
        return increaseWithActionLog("BOARD", "POST", boardSeq, "DISLIKE", () -> boardMapper.increaseBoardDislikeCount(boardSeq));
    }

    @Override
    @Transactional
    public int reportBoard(Long boardSeq) {
        BoardDto before = boardMapper.findBoardById(boardSeq);
        if (before == null) {
            return 0;
        }
        if (boardBlindSupport.isBlind(before)) {
            return 0;
        }
        long rcBefore = before.getReportCount() != null ? before.getReportCount() : 0L;
        int n = increaseWithActionLog("BOARD", "POST", boardSeq, "REPORT", () -> boardMapper.increaseBoardReportCount(boardSeq));
        if (n > 0) {
            BoardDto after = boardMapper.findBoardById(boardSeq);
            if (after != null) {
                long rcAfter = after.getReportCount() != null ? after.getReportCount() : 0L;
                if (rcBefore < boardBlindSupport.getBlindReportThreshold() && rcAfter >= boardBlindSupport.getBlindReportThreshold()) {
                    Long writer = after.getWriterMemberSeq();
                    if (writer != null && writer > 0) {
                        try {
                            walletPointGrantService.deductFreeBoardPostOnBlind(writer, boardSeq);
                        } catch (Exception e) {
                            log.warn("블라인드 포인트 회수 실패 boardSeq={}: {}", boardSeq, e.toString());
                        }
                    }
                }
            }
        }
        return n;
    }

    private int increaseWithActionLog(String boardKind,
                                      String targetKind,
                                      Long targetSeq,
                                      String actionType,
                                      CounterUpdater counterUpdater) {
        Long memberSeq = RequestContext.getLoginMemberSeq();
        String memberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        int inserted = boardMapper.insertBoardActionLog(
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
}
