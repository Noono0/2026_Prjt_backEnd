package com.noonoo.prjtbackend.contentfilter.serviceImpl;

import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordDto;
import com.noonoo.prjtbackend.contentfilter.mapper.ContentFilterWordMapper;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ContentFilterApplyServiceImpl implements ContentFilterApplyService {

    private final ContentFilterWordMapper contentFilterWordMapper;

    /** replace: * 치환 | reject: 금칙어 시 예외 */
    @Value("${app.content-filter.mode:replace}")
    private String contentFilterMode;

    private volatile List<ContentFilterWordDto> cache;
    private final Object cacheLock = new Object();

    @Override
    public void refreshCache() {
        synchronized (cacheLock) {
            cache = null;
        }
    }

    private List<ContentFilterWordDto> getWords() {
        List<ContentFilterWordDto> c = cache;
        if (c != null) {
            return c;
        }
        synchronized (cacheLock) {
            if (cache == null) {
                List<ContentFilterWordDto> loaded = contentFilterWordMapper.selectAllActiveForFilter();
                cache = loaded != null ? List.copyOf(loaded) : List.of();
            }
            return cache;
        }
    }

    private boolean isRejectMode() {
        return contentFilterMode != null && "reject".equalsIgnoreCase(contentFilterMode.trim());
    }

    /**
     * 긴 키워드 우선(치환 로직과 동일 순서).
     */
    private Optional<ContentFilterWordDto> findFirstMatchingWord(String text) {
        if (!StringUtils.hasText(text)) {
            return Optional.empty();
        }
        List<ContentFilterWordDto> words = new ArrayList<>(getWords());
        words.sort(Comparator.comparingInt((ContentFilterWordDto w) ->
                w.getKeyword() != null ? -w.getKeyword().length() : 0));
        for (ContentFilterWordDto w : words) {
            String k = w.getKeyword();
            if (!StringUtils.hasText(k)) {
                continue;
            }
            String safe = k.trim();
            if (safe.isEmpty()) {
                continue;
            }
            Pattern p = Pattern.compile(Pattern.quote(safe), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            if (p.matcher(text).find()) {
                return Optional.of(w);
            }
        }
        return Optional.empty();
    }

    @Override
    public String applyField(String fieldLabel, String text) {
        if (!StringUtils.hasText(text)) {
            return text;
        }
        if (isRejectMode()) {
            if (findFirstMatchingWord(text).isPresent()) {
                throw new IllegalArgumentException(fieldLabel + "에 비속어가 포함되어 있습니다.");
            }
            return text;
        }
        return filterText(text);
    }

    @Override
    public String filterText(String text) {
        if (!StringUtils.hasText(text)) {
            return text;
        }
        List<ContentFilterWordDto> words = new ArrayList<>(getWords());
        words.sort(Comparator.comparingInt((ContentFilterWordDto w) ->
                w.getKeyword() != null ? -w.getKeyword().length() : 0));
        String result = text;
        for (ContentFilterWordDto w : words) {
            String k = w.getKeyword();
            if (!StringUtils.hasText(k)) {
                continue;
            }
            String safe = k.trim();
            if (safe.isEmpty()) {
                continue;
            }
            int len = Math.min(safe.length(), 40);
            String replacement = "*".repeat(len);
            Pattern p = Pattern.compile(Pattern.quote(safe), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            result = p.matcher(result).replaceAll(Matcher.quoteReplacement(replacement));
        }
        return result;
    }
}
