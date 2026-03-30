package com.noonoo.prjtbackend.noticeBoard.controller;

import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardCommentUpdateRequest;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.noticeBoard.service.NoticeBoardCommentService;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/notice-boards")
@RequiredArgsConstructor
public class NoticeBoardCommentController {

    private final NoticeBoardCommentService noticeBoardCommentService;

    @GetMapping("/{noticeBoardSeq}/comments")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<List<BoardCommentDto>> listComments(@PathVariable Long noticeBoardSeq,
                                                           @RequestParam(defaultValue = "latest") String sort) {
        List<BoardCommentDto> items = noticeBoardCommentService.findComments(noticeBoardSeq, sort);
        return ApiResponse.ok("댓글 목록 조회 완료", items);
    }

    @PostMapping("/{noticeBoardSeq}/comments")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Long> createComment(@PathVariable Long noticeBoardSeq,
                                           @RequestBody BoardCommentSaveRequest request) {
        request.setBoardSeq(noticeBoardSeq);
        long seq = noticeBoardCommentService.createComment(request);
        return ApiResponse.ok("댓글이 등록되었습니다.", seq);
    }

    @PostMapping("/{noticeBoardSeq}/comments/{commentSeq}/like")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> likeComment(@PathVariable Long noticeBoardSeq, @PathVariable Long commentSeq) {
        int result = noticeBoardCommentService.likeComment(noticeBoardSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "처리 완료" : "이미 추천한 댓글입니다.", result);
    }

    @PostMapping("/{noticeBoardSeq}/comments/{commentSeq}/dislike")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> dislikeComment(@PathVariable Long noticeBoardSeq, @PathVariable Long commentSeq) {
        int result = noticeBoardCommentService.dislikeComment(noticeBoardSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "처리 완료" : "이미 비추천한 댓글입니다.", result);
    }

    @PostMapping("/{noticeBoardSeq}/comments/{commentSeq}/report")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> reportComment(@PathVariable Long noticeBoardSeq, @PathVariable Long commentSeq) {
        int result = noticeBoardCommentService.reportComment(noticeBoardSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "신고가 접수되었습니다." : "이미 신고한 댓글입니다.", result);
    }

    @PutMapping("/{noticeBoardSeq}/comments/{commentSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> updateComment(@PathVariable Long noticeBoardSeq,
                                              @PathVariable Long commentSeq,
                                              @RequestBody BoardCommentUpdateRequest body) {
        int result = noticeBoardCommentService.updateComment(noticeBoardSeq, commentSeq, body);
        return ApiResponse.ok(result > 0 ? "댓글이 수정되었습니다." : "댓글 수정에 실패했습니다.", result);
    }

    @DeleteMapping("/{noticeBoardSeq}/comments/{commentSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.NOTICE_BOARD + "')")
    public ApiResponse<Integer> deleteComment(@PathVariable Long noticeBoardSeq, @PathVariable Long commentSeq) {
        int result = noticeBoardCommentService.deleteComment(noticeBoardSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "댓글이 삭제되었습니다." : "댓글 삭제에 실패했습니다.", result);
    }
}
