package com.noonoo.prjtbackend.blacklistreport.support;

import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BlacklistReportExcelExporter {

    /** 쿼리스트링 등: 쉼표로 구분된 camelCase 필드명. 비어 있으면 {@link #DEFAULT_COLUMN_KEYS} 사용 */
    public static final List<String> DEFAULT_COLUMN_KEYS = List.of(
            "blacklistReportSeq",
            "blacklistTargetId",
            "title",
            "writerName",
            "viewCount",
            "createDt",
            "content"
    );

    private static final Map<String, String> HEADER_BY_KEY = new LinkedHashMap<>();
    private static final Set<String> ALLOWED_KEYS;

    static {
        HEADER_BY_KEY.put("blacklistReportSeq", "번호");
        HEADER_BY_KEY.put("blacklistTargetId", "블랙리스트 아이디");
        HEADER_BY_KEY.put("title", "제목");
        HEADER_BY_KEY.put("content", "내용(텍스트)");
        HEADER_BY_KEY.put("writerMemberSeq", "작성자 회원번호");
        HEADER_BY_KEY.put("writerMemberId", "작성자 아이디");
        HEADER_BY_KEY.put("writerName", "작성자");
        HEADER_BY_KEY.put("writerProfileImageUrl", "작성자 프로필 URL");
        HEADER_BY_KEY.put("categoryCode", "카테고리 코드");
        HEADER_BY_KEY.put("categoryName", "카테고리");
        HEADER_BY_KEY.put("viewCount", "조회");
        HEADER_BY_KEY.put("likeCount", "추천");
        HEADER_BY_KEY.put("dislikeCount", "비추천");
        HEADER_BY_KEY.put("commentCount", "댓글 수");
        HEADER_BY_KEY.put("reportCount", "신고 수");
        HEADER_BY_KEY.put("commentAllowedYn", "댓글 허용");
        HEADER_BY_KEY.put("replyAllowedYn", "답글 허용");
        HEADER_BY_KEY.put("createDt", "작성일시");
        HEADER_BY_KEY.put("modifyDt", "수정일시");
        ALLOWED_KEYS = Collections.unmodifiableSet(new LinkedHashSet<>(HEADER_BY_KEY.keySet()));
    }

    private BlacklistReportExcelExporter() {
    }

    public static byte[] toXlsx(List<BlacklistReportDto> rows, List<String> requestedColumnKeys) throws IOException {
        List<String> keys = normalizeColumnKeys(requestedColumnKeys);
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("블랙리스트 제보");
            Row head = sheet.createRow(0);
            for (int c = 0; c < keys.size(); c++) {
                head.createCell(c).setCellValue(HEADER_BY_KEY.get(keys.get(c)));
            }

            int r = 1;
            for (BlacklistReportDto d : rows) {
                Row row = sheet.createRow(r++);
                for (int c = 0; c < keys.size(); c++) {
                    writeCell(row, c, keys.get(c), d);
                }
            }

            for (int i = 0; i < keys.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(bos);
            return bos.toByteArray();
        }
    }

    private static List<String> normalizeColumnKeys(List<String> requested) {
        if (requested == null || requested.isEmpty()) {
            return new ArrayList<>(DEFAULT_COLUMN_KEYS);
        }
        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String raw : requested) {
            if (raw == null) {
                continue;
            }
            String k = raw.trim();
            if (k.isEmpty()) {
                continue;
            }
            if (!ALLOWED_KEYS.contains(k)) {
                continue;
            }
            out.add(k);
        }
        if (out.isEmpty()) {
            return new ArrayList<>(DEFAULT_COLUMN_KEYS);
        }
        return new ArrayList<>(out);
    }

    /** 허용된 필드명(정규화된 소문자 아님, camelCase 그대로). 프론트와 동일 키 사용 */
    public static Set<String> allowedKeys() {
        return ALLOWED_KEYS;
    }

    public static List<String> parseKeysQuery(String columnsCsv) {
        if (columnsCsv == null || columnsCsv.isBlank()) {
            return List.of();
        }
        String[] parts = columnsCsv.split(",");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            if (p != null && !p.isBlank()) {
                list.add(p.trim());
            }
        }
        return list;
    }

    private static void writeCell(Row row, int colIndex, String key, BlacklistReportDto d) {
        switch (key) {
            case "blacklistReportSeq" ->
                    row.createCell(colIndex).setCellValue(d.getBlacklistReportSeq() != null ? d.getBlacklistReportSeq() : 0);
            case "blacklistTargetId" -> row.createCell(colIndex).setCellValue(nullToEmpty(d.getBlacklistTargetId()));
            case "title" -> row.createCell(colIndex).setCellValue(nullToEmpty(d.getTitle()));
            case "content" -> {
                String plain = stripHtml(d.getContent());
                if (plain.length() > 32000) {
                    plain = plain.substring(0, 32000) + "…";
                }
                row.createCell(colIndex).setCellValue(plain);
            }
            case "writerMemberSeq" ->
                    row.createCell(colIndex).setCellValue(d.getWriterMemberSeq() != null ? d.getWriterMemberSeq() : 0);
            case "writerMemberId" -> row.createCell(colIndex).setCellValue(nullToEmpty(d.getWriterMemberId()));
            case "writerName" -> row.createCell(colIndex).setCellValue(nullToEmpty(d.getWriterName()));
            case "writerProfileImageUrl" -> row.createCell(colIndex).setCellValue(nullToEmpty(d.getWriterProfileImageUrl()));
            case "categoryCode" -> row.createCell(colIndex).setCellValue(nullToEmpty(d.getCategoryCode()));
            case "categoryName" -> row.createCell(colIndex).setCellValue(nullToEmpty(d.getCategoryName()));
            case "viewCount" -> row.createCell(colIndex).setCellValue(d.getViewCount() != null ? d.getViewCount() : 0);
            case "likeCount" -> row.createCell(colIndex).setCellValue(d.getLikeCount() != null ? d.getLikeCount() : 0);
            case "dislikeCount" -> row.createCell(colIndex).setCellValue(d.getDislikeCount() != null ? d.getDislikeCount() : 0);
            case "commentCount" -> row.createCell(colIndex).setCellValue(d.getCommentCount() != null ? d.getCommentCount() : 0);
            case "reportCount" -> row.createCell(colIndex).setCellValue(d.getReportCount() != null ? d.getReportCount() : 0);
            case "commentAllowedYn" -> row.createCell(colIndex).setCellValue(nullToEmpty(d.getCommentAllowedYn()));
            case "replyAllowedYn" -> row.createCell(colIndex).setCellValue(nullToEmpty(d.getReplyAllowedYn()));
            case "createDt" -> row.createCell(colIndex).setCellValue(nullToEmpty(d.getCreateDt()));
            case "modifyDt" -> row.createCell(colIndex).setCellValue(nullToEmpty(d.getModifyDt()));
            default -> row.createCell(colIndex).setCellValue("");
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String stripHtml(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        return html.replaceAll("<[^>]+>", " ").replaceAll("&nbsp;", " ").replaceAll("\\s+", " ").trim();
    }
}
