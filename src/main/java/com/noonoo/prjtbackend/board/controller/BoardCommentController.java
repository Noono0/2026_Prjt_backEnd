package com.noonoo.prjtbackend.board.controller;

import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardCommentUpdateRequest;
import com.noonoo.prjtbackend.board.service.BoardCommentService;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    @GetMapping("/{boardSeq}/comments")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<List<BoardCommentDto>> listComments(@PathVariable Long boardSeq,
                                                            @RequestParam(defaultValue = "latest") String sort) {
        List<BoardCommentDto> items = boardCommentService.findComments(boardSeq, sort);
        return ApiResponse.ok("댓글 목록 조회 완료", items);
    }

    @PostMapping("/{boardSeq}/comments")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Long> createComment(@PathVariable Long boardSeq,
                                           @RequestBody BoardCommentSaveRequest request) {
        request.setBoardSeq(boardSeq);
        long seq = boardCommentService.createComment(request);
        return ApiResponse.ok("댓글이 등록되었습니다.", seq);
    }

    @PostMapping("/{boardSeq}/comments/{commentSeq}/like")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> likeComment(@PathVariable Long boardSeq, @PathVariable Long commentSeq) {
        int result = boardCommentService.likeComment(boardSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "처리 완료" : "이미 추천한 댓글입니다.", result);
    }

    @PostMapping("/{boardSeq}/comments/{commentSeq}/dislike")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> dislikeComment(@PathVariable Long boardSeq, @PathVariable Long commentSeq) {
        int result = boardCommentService.dislikeComment(boardSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "처리 완료" : "이미 비추천한 댓글입니다.", result);
    }

    @PostMapping("/{boardSeq}/comments/{commentSeq}/report")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> reportComment(@PathVariable Long boardSeq, @PathVariable Long commentSeq) {
        int result = boardCommentService.reportComment(boardSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "신고가 접수되었습니다." : "이미 신고한 댓글입니다.", result);
    }

    @PutMapping("/{boardSeq}/comments/{commentSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> updateComment(@PathVariable Long boardSeq,
                                              @PathVariable Long commentSeq,
                                              @RequestBody BoardCommentUpdateRequest body) {
        int result = boardCommentService.updateComment(boardSeq, commentSeq, body);
        return ApiResponse.ok(result > 0 ? "댓글이 수정되었습니다." : "댓글 수정에 실패했습니다.", result);
    }

    @DeleteMapping("/{boardSeq}/comments/{commentSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BOARD + "')")
    public ApiResponse<Integer> deleteComment(@PathVariable Long boardSeq, @PathVariable Long commentSeq) {
        int result = boardCommentService.deleteComment(boardSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "댓글이 삭제되었습니다." : "댓글 삭제에 실패했습니다.", result);
    }
}
