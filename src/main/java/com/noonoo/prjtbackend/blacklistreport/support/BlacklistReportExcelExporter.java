package com.noonoo.prjtbackend.blacklistreport.support;

import com.noonoo.prjtbackend.blacklistreport.dto.BlacklistReportDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public final class BlacklistReportExcelExporter {

    private BlacklistReportExcelExporter() {
    }

    public static byte[] toXlsx(List<BlacklistReportDto> rows) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("블랙리스트 제보");
            Row head = sheet.createRow(0);
            head.createCell(0).setCellValue("번호");
            head.createCell(1).setCellValue("블랙리스트 아이디");
            head.createCell(2).setCellValue("제목");
            head.createCell(3).setCellValue("작성자");
            head.createCell(4).setCellValue("조회");
            head.createCell(5).setCellValue("작성일시");
            head.createCell(6).setCellValue("내용(텍스트)");

            int r = 1;
            for (BlacklistReportDto d : rows) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(d.getBlacklistReportSeq() != null ? d.getBlacklistReportSeq() : 0);
                row.createCell(1).setCellValue(nullToEmpty(d.getBlacklistTargetId()));
                row.createCell(2).setCellValue(nullToEmpty(d.getTitle()));
                row.createCell(3).setCellValue(nullToEmpty(d.getWriterName()));
                row.createCell(4).setCellValue(d.getViewCount() != null ? d.getViewCount() : 0);
                row.createCell(5).setCellValue(nullToEmpty(d.getCreateDt()));
                String plain = stripHtml(d.getContent());
                if (plain.length() > 32000) {
                    plain = plain.substring(0, 32000) + "…";
                }
                row.createCell(6).setCellValue(plain);
            }

            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(bos);
            return bos.toByteArray();
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
