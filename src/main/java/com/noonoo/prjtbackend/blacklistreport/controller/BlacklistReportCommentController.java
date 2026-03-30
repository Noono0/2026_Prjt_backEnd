package com.noonoo.prjtbackend.blacklistreport.controller;

import com.noonoo.prjtbackend.board.dto.BoardCommentDto;
import com.noonoo.prjtbackend.board.dto.BoardCommentSaveRequest;
import com.noonoo.prjtbackend.board.dto.BoardCommentUpdateRequest;
import com.noonoo.prjtbackend.blacklistreport.service.BlacklistReportCommentService;
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
@RequestMapping("/api/blacklist-reports")
@RequiredArgsConstructor
public class BlacklistReportCommentController {

    private final BlacklistReportCommentService blacklistReportCommentService;

    @GetMapping("/{blacklistReportSeq}/comments")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<List<BoardCommentDto>> listComments(@PathVariable Long blacklistReportSeq,
                                                           @RequestParam(defaultValue = "latest") String sort) {
        List<BoardCommentDto> items = blacklistReportCommentService.findComments(blacklistReportSeq, sort);
        return ApiResponse.ok("댓글 목록 조회 완료", items);
    }

    @PostMapping("/{blacklistReportSeq}/comments")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Long> createComment(@PathVariable Long blacklistReportSeq,
                                           @RequestBody BoardCommentSaveRequest request) {
        request.setBoardSeq(blacklistReportSeq);
        long seq = blacklistReportCommentService.createComment(request);
        return ApiResponse.ok("댓글이 등록되었습니다.", seq);
    }

    @PostMapping("/{blacklistReportSeq}/comments/{commentSeq}/like")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> likeComment(@PathVariable Long blacklistReportSeq, @PathVariable Long commentSeq) {
        int result = blacklistReportCommentService.likeComment(blacklistReportSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "처리 완료" : "이미 추천한 댓글입니다.", result);
    }

    @PostMapping("/{blacklistReportSeq}/comments/{commentSeq}/dislike")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> dislikeComment(@PathVariable Long blacklistReportSeq, @PathVariable Long commentSeq) {
        int result = blacklistReportCommentService.dislikeComment(blacklistReportSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "처리 완료" : "이미 비추천한 댓글입니다.", result);
    }

    @PostMapping("/{blacklistReportSeq}/comments/{commentSeq}/report")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> reportComment(@PathVariable Long blacklistReportSeq, @PathVariable Long commentSeq) {
        int result = blacklistReportCommentService.reportComment(blacklistReportSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "신고가 접수되었습니다." : "이미 신고한 댓글입니다.", result);
    }

    @PutMapping("/{blacklistReportSeq}/comments/{commentSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> updateComment(@PathVariable Long blacklistReportSeq,
                                            @PathVariable Long commentSeq,
                                            @RequestBody BoardCommentUpdateRequest body) {
        int result = blacklistReportCommentService.updateComment(blacklistReportSeq, commentSeq, body);
        return ApiResponse.ok(result > 0 ? "댓글이 수정되었습니다." : "댓글 수정에 실패했습니다.", result);
    }

    @DeleteMapping("/{blacklistReportSeq}/comments/{commentSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> deleteComment(@PathVariable Long blacklistReportSeq, @PathVariable Long commentSeq) {
        int result = blacklistReportCommentService.deleteComment(blacklistReportSeq, commentSeq);
        return ApiResponse.ok(result > 0 ? "댓글이 삭제되었습니다." : "댓글 삭제에 실패했습니다.", result);
    }
}
