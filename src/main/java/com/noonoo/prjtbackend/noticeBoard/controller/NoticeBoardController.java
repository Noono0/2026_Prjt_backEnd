package com.noonoo.prjtbackend.noticeBoard.controller;

import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardDto;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardSaveRequest;
import com.noonoo.prjtbackend.noticeBoard.dto.NoticeBoardSearchCondition;
import com.noonoo.prjtbackend.noticeBoard.service.NoticeBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notice-boards")
@RequiredArgsConstructor
public class NoticeBoardController {

    private final NoticeBoardService noticeBoardService;

    @GetMapping("/categories")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<List<OptionDto>> findNoticeCategories() {
        List<OptionDto> result = noticeBoardService.findNoticeCategoryOptions();
        return ApiResponse.ok("공지사항 카테고리 조회 완료", result);
    }

    /**
     * 자유게시판 상단 고정 공지(공지사항 모듈에서 pin_on_free_board_yn=Y 인 글).
     * 자유게시판 메뉴 조회 권한으로 호출합니다.
     */
    @GetMapping("/pin-on-free-board")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<List<NoticeBoardDto>> findNoticeBoardsPinnedOnFreeBoard() {
        List<NoticeBoardDto> items = noticeBoardService.findNoticeBoardsPinnedOnFreeBoard();
        return ApiResponse.ok("자유게시판 상단 고정 공지 조회 완료", items);
    }

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<PageResponse<NoticeBoardDto>> searchNoticeBoards(@RequestBody NoticeBoardSearchCondition request) {
        log.info("=======> /api/notice-boards/search param={}", request);
        PageResponse<NoticeBoardDto> result = noticeBoardService.findNoticeBoards(request);
        return ApiResponse.ok("공지사항 목록 조회 완료", result);
    }

    @GetMapping("/detail/{noticeBoardSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<NoticeBoardDto> findNoticeBoardDetail(@PathVariable Long noticeBoardSeq) {
        log.info("=======> /api/notice-boards/detail param={}", noticeBoardSeq);
        NoticeBoardDto detail = noticeBoardService.findNoticeBoardDetail(noticeBoardSeq);
        return ApiResponse.ok("공지사항 상세 조회 완료", detail);
    }

    @PostMapping("/{noticeBoardSeq}/view")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> increaseViewCount(@PathVariable Long noticeBoardSeq) {
        int result = noticeBoardService.increaseViewCount(noticeBoardSeq);
        return ApiResponse.ok(result > 0 ? "조회수 증가 완료" : "조회수 증가 실패", result);
    }

    @PostMapping("/{noticeBoardSeq}/like")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> likeNoticeBoard(@PathVariable Long noticeBoardSeq) {
        int result = noticeBoardService.likeNoticeBoard(noticeBoardSeq);
        return ApiResponse.ok(result > 0 ? "좋아요 처리 완료" : "이미 처리된 좋아요입니다.", result);
    }

    @PostMapping("/{noticeBoardSeq}/dislike")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> dislikeNoticeBoard(@PathVariable Long noticeBoardSeq) {
        int result = noticeBoardService.dislikeNoticeBoard(noticeBoardSeq);
        return ApiResponse.ok(result > 0 ? "싫어요 처리 완료" : "이미 처리된 싫어요입니다.", result);
    }

    @PostMapping("/{noticeBoardSeq}/report")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> reportNoticeBoard(@PathVariable Long noticeBoardSeq) {
        int result = noticeBoardService.reportNoticeBoard(noticeBoardSeq);
        return ApiResponse.ok(result > 0 ? "신고 처리 완료" : "이미 처리된 신고입니다.", result);
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> createNoticeBoard(@RequestBody NoticeBoardSaveRequest request) {
        log.info("=======> /api/notice-boards/create param={}", request);
        int result = noticeBoardService.createNoticeBoard(request);
        return ApiResponse.ok(result > 0 ? "공지사항 등록 완료" : "공지사항 등록 실패", result);
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> updateNoticeBoard(@RequestBody NoticeBoardSaveRequest request) {
        log.info("=======> /api/notice-boards/update param={}", request);
        int result = noticeBoardService.updateNoticeBoard(request);
        return ApiResponse.ok(result > 0 ? "공지사항 수정 완료" : "공지사항 수정 실패", result);
    }

    @DeleteMapping("/delete/{noticeBoardSeq}")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> deleteNoticeBoard(@PathVariable Long noticeBoardSeq) {
        log.info("=======> /api/notice-boards/delete param={}", noticeBoardSeq);
        int result = noticeBoardService.deleteNoticeBoard(noticeBoardSeq);
        return ApiResponse.ok(result > 0 ? "공지사항 삭제 완료" : "공지사항 삭제 실패", result);
    }
}
