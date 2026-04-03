package com.noonoo.prjtbackend.board.controller;

import com.noonoo.prjtbackend.board.dto.BoardDto;
import com.noonoo.prjtbackend.board.dto.BoardSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardSearchCondition;
import com.noonoo.prjtbackend.board.serviceImpl.BoardServiceImpl;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/inquiry-boards")
@RequiredArgsConstructor
public class InquiryBoardController {
    @GetMapping("/categories")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<java.util.List<OptionDto>> categories() {
        return ApiResponse.ok("문의게시판 카테고리 조회 완료", boardService.findInquiryCategoryOptions());
    }


    private final BoardServiceImpl boardService;

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<PageResponse<BoardDto>> search(@RequestBody BoardSearchCondition request) {
        return ApiResponse.ok("문의게시판 목록 조회 완료", boardService.findInquiryBoards(request));
    }

    @GetMapping("/detail/{boardSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<BoardDto> detail(@PathVariable Long boardSeq,
                                        @RequestParam(required = false) String password) {
        BoardDto detail = boardService.findInquiryBoardDetail(boardSeq, password);
        if (detail == null) {
            return ApiResponse.fail("NOT_FOUND", "게시글을 찾을 수 없습니다.");
        }
        return ApiResponse.ok("문의게시판 상세 조회 완료", detail);
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> create(@RequestBody BoardSaveRequest request) {
        int n = boardService.createInquiryBoard(request);
        return ApiResponse.ok(n > 0 ? "문의게시판 등록 완료" : "문의게시판 등록 실패", n);
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> update(@RequestBody BoardSaveRequest request) {
        int n = boardService.updateInquiryBoard(request);
        return ApiResponse.ok(n > 0 ? "문의게시판 수정 완료" : "문의게시판 수정 실패", n);
    }

    @DeleteMapping("/mine/{boardSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> deleteMine(@PathVariable Long boardSeq) {
        int n = boardService.deleteBoardIfWriter(boardSeq);
        return ApiResponse.ok(n > 0 ? "삭제되었습니다." : "삭제할 수 없습니다.", n);
    }

    @PostMapping("/{boardSeq}/view")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> increaseView(@PathVariable Long boardSeq) {
        int n = boardService.increaseViewCount(boardSeq);
        return ApiResponse.ok(n > 0 ? "조회수 증가 완료" : "조회수 증가 실패", n);
    }

    @PostMapping("/{boardSeq}/like")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> like(@PathVariable Long boardSeq) {
        int n = boardService.likeBoard(boardSeq);
        return ApiResponse.ok(n > 0 ? "좋아요 처리 완료" : "이미 처리된 좋아요입니다.", n);
    }

    @PostMapping("/{boardSeq}/dislike")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> dislike(@PathVariable Long boardSeq) {
        int n = boardService.dislikeBoard(boardSeq);
        return ApiResponse.ok(n > 0 ? "싫어요 처리 완료" : "이미 처리된 싫어요입니다.", n);
    }

    @PostMapping("/{boardSeq}/report")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> report(@PathVariable Long boardSeq) {
        int n = boardService.reportBoard(boardSeq);
        return ApiResponse.ok(n > 0 ? "신고 처리 완료" : "이미 처리된 신고입니다.", n);
    }
}

