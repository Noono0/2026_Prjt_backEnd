package com.noonoo.prjtbackend.blacklistreport.controller;

import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportDto;
import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportSaveRequest;
import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportSearchCondition;
import com.noonoo.prjtbackend.blacklistreport.service.BlacklistReportService;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/blacklist-reports")
@RequiredArgsConstructor
public class BlacklistReportController {

    private final BlacklistReportService blacklistReportService;

    @GetMapping("/categories")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<List<OptionDto>> categories() {
        return ApiResponse.ok("블랙리스트 카테고리(등록·수정)", blacklistReportService.findBlacklistCategoryOptions());
    }

    @GetMapping("/list-categories")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<List<OptionDto>> listCategories() {
        return ApiResponse.ok("블랙리스트 목록 조회 카테고리", blacklistReportService.findBlacklistListCategoryOptions());
    }

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<PageResponse<BlacklistReportDto>> search(@RequestBody BlacklistReportSearchCondition request) {
        log.info("POST /api/blacklist-reports/search param={}", request);
        PageResponse<BlacklistReportDto> result = blacklistReportService.findBlacklistReports(request);
        return ApiResponse.ok("블랙리스트 제보 목록 조회 완료", result);
    }

    @GetMapping("/detail/{blacklistReportSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<BlacklistReportDto> detail(@PathVariable Long blacklistReportSeq) {
        BlacklistReportDto d = blacklistReportService.findDetail(blacklistReportSeq);
        if (d == null) {
            return ApiResponse.fail("NOT_FOUND", "게시글을 찾을 수 없습니다.");
        }
        return ApiResponse.ok("블랙리스트 제보 상세 조회 완료", d);
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> create(@RequestBody BlacklistReportSaveRequest request) {
        int n = blacklistReportService.create(request);
        return ApiResponse.ok(n > 0 ? "등록되었습니다." : "등록에 실패했습니다.", n);
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> update(@RequestBody BlacklistReportSaveRequest request) {
        int n = blacklistReportService.update(request);
        return ApiResponse.ok(n > 0 ? "수정되었습니다." : "수정할 수 없습니다.", n);
    }

    @DeleteMapping("/mine/{blacklistReportSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> deleteMine(@PathVariable Long blacklistReportSeq) {
        int n = blacklistReportService.deleteIfWriter(blacklistReportSeq);
        return ApiResponse.ok(n > 0 ? "삭제되었습니다." : "삭제할 수 없습니다.", n);
    }

    @PostMapping("/{blacklistReportSeq}/view")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> view(@PathVariable Long blacklistReportSeq) {
        int n = blacklistReportService.increaseViewCount(blacklistReportSeq);
        return ApiResponse.ok("조회수 반영", n);
    }

    @PostMapping("/{blacklistReportSeq}/like")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> like(@PathVariable Long blacklistReportSeq) {
        int n = blacklistReportService.likeBlacklistReport(blacklistReportSeq);
        return ApiResponse.ok(n > 0 ? "추천되었습니다." : "이미 추천한 글입니다.", n);
    }

    @PostMapping("/{blacklistReportSeq}/dislike")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> dislike(@PathVariable Long blacklistReportSeq) {
        int n = blacklistReportService.dislikeBlacklistReport(blacklistReportSeq);
        return ApiResponse.ok(n > 0 ? "비추천되었습니다." : "이미 비추천한 글입니다.", n);
    }

    @PostMapping("/{blacklistReportSeq}/report")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ApiResponse<Integer> report(@PathVariable Long blacklistReportSeq) {
        int n = blacklistReportService.reportBlacklistReport(blacklistReportSeq);
        return ApiResponse.ok(n > 0 ? "신고가 접수되었습니다." : "이미 신고한 글입니다.", n);
    }

    @GetMapping("/export")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.BLACKLIST_REPORT + "')")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) String blacklistTargetId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String createDtFrom,
            @RequestParam(required = false) String createDtTo) throws Exception {
        byte[] bytes = blacklistReportService.exportExcel(blacklistTargetId, keyword, createDtFrom, createDtTo);
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        // ASCII 파일명만 사용 (UTF-8 filename* 는 프록시/브라우저에서 깨지는 경우가 있음)
        String filename = "blacklist-report-" + ts + ".xlsx";
        String cd = "attachment; filename=\"" + filename + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, cd)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
