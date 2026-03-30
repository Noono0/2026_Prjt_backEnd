package com.noonoo.prjtbackend.board.controller;

import com.noonoo.prjtbackend.board.dto.BoardDto;
import com.noonoo.prjtbackend.board.dto.BoardPopularConfigDto;
import com.noonoo.prjtbackend.board.dto.BoardSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardSearchCondition;
import com.noonoo.prjtbackend.board.service.BoardService;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/categories")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<List<OptionDto>> findBoardCategories() {
        List<OptionDto> result = boardService.findBoardCategoryOptions();
        return ApiResponse.ok("자유게시판 카테고리 조회 완료", result);
    }

    @GetMapping("/popular-config")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<BoardPopularConfigDto> getBoardPopularConfig() {
        BoardPopularConfigDto result = boardService.getBoardPopularConfig();
        return ApiResponse.ok("자유게시판 인기글 설정 조회 완료", result);
    }

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<PageResponse<BoardDto>> searchBoards(@RequestBody BoardSearchCondition request) {
        log.info("=======> /api/boards/search param={}", request);
        PageResponse<BoardDto> result = boardService.findBoards(request);
        return ApiResponse.ok("자유게시판 목록 조회 완료", result);
    }

    @GetMapping("/detail/{boardSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<BoardDto> findBoardDetail(@PathVariable Long boardSeq) {
        log.info("=======> /api/boards/detail param={}", boardSeq);
        BoardDto detail = boardService.findBoardDetail(boardSeq);
        if (detail == null) {
            return ApiResponse.fail("NOT_FOUND", "게시글을 찾을 수 없거나 블라인드 처리된 글입니다.");
        }
        return ApiResponse.ok("자유게시판 상세 조회 완료", detail);
    }

    @PostMapping("/{boardSeq}/view")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> increaseViewCount(@PathVariable Long boardSeq) {
        int result = boardService.increaseViewCount(boardSeq);
        return ApiResponse.ok(result > 0 ? "조회수 증가 완료" : "조회수 증가 실패", result);
    }

    @PostMapping("/{boardSeq}/like")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> likeBoard(@PathVariable Long boardSeq) {
        int result = boardService.likeBoard(boardSeq);
        return ApiResponse.ok(result > 0 ? "좋아요 처리 완료" : "이미 처리된 좋아요입니다.", result);
    }

    @PostMapping("/{boardSeq}/dislike")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> dislikeBoard(@PathVariable Long boardSeq) {
        int result = boardService.dislikeBoard(boardSeq);
        return ApiResponse.ok(result > 0 ? "싫어요 처리 완료" : "이미 처리된 싫어요입니다.", result);
    }

    @PostMapping("/{boardSeq}/report")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> reportBoard(@PathVariable Long boardSeq) {
        int result = boardService.reportBoard(boardSeq);
        return ApiResponse.ok(result > 0 ? "신고 처리 완료" : "이미 처리된 신고입니다.", result);
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> createBoard(@RequestBody BoardSaveRequest request) {
        log.info("=======> /api/boards/create param={}", request);
        int result = boardService.createBoard(request);
        return ApiResponse.ok(result > 0 ? "자유게시판 등록 완료" : "자유게시판 등록 실패", result);
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> updateBoard(@RequestBody BoardSaveRequest request) {
        log.info("=======> /api/boards/update param={}", request);
        int result = boardService.updateBoard(request);
        return ApiResponse.ok(result > 0 ? "자유게시판 수정 완료" : "자유게시판 수정 실패", result);
    }

    @DeleteMapping("/delete/{boardSeq}")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> deleteBoard(@PathVariable Long boardSeq) {
        log.info("=======> /api/boards/delete param={}", boardSeq);
        int result = boardService.deleteBoard(boardSeq);
        return ApiResponse.ok(result > 0 ? "자유게시판 삭제 완료" : "자유게시판 삭제 실패", result);
    }

    /** 작성자 본인 소프트 삭제(use_yn = N). Spring 세션 회원은 isAuthenticated()와 무관할 수 있어 상세·목록과 동일 권한 + 서비스에서 작성자 검증 */
    @DeleteMapping("/mine/{boardSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> deleteMyBoard(@PathVariable Long boardSeq) {
        log.info("=======> /api/boards/mine/{} delete", boardSeq);
        int result = boardService.deleteBoardIfWriter(boardSeq);
        return ApiResponse.ok(result > 0 ? "삭제되었습니다." : "삭제할 수 없습니다.", result);
    }
}
