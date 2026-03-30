package com.noonoo.prjtbackend.contentfilter.service;

/**
 * DB 등록 키워드로 제목·본문·댓글 등 텍스트 처리.
 * <p>{@code app.content-filter.mode=replace}: * 치환 후 저장.</p>
 * <p>{@code app.content-filter.mode=reject}: 금칙어 포함 시 {@link IllegalArgumentException}으로 저장 거부.</p>
 */
public interface ContentFilterApplyService {

    /**
     * 설정에 따라 치환(replace)하거나, reject 모드에서는 금칙어가 있으면 예외를 던지고 원문을 반환하지 않음.
     *
     * @param fieldLabel 오류 메시지용 (예: "제목", "본문", "댓글 내용")
     */
    String applyField(String fieldLabel, String text);

    /** null/공백은 그대로 — 내부·직접 치환이 필요할 때만 사용 */
    String filterText(String text);

    void refreshCache();
}
