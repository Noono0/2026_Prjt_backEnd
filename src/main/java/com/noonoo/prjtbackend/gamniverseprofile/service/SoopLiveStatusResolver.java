package com.noonoo.prjtbackend.gamniverseprofile.service;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class SoopLiveStatusResolver implements DisposableBean {

    private static final long CACHE_TTL_MILLIS = 5_000L;
    private static final Pattern ROOM_PATH_PATTERN = Pattern.compile("^/([^/]+)/([^/]+)$");
    private static final double NAVIGATE_TIMEOUT_MS = 8_000;
    private static final double JS_SETTLE_WAIT_MS = 1_200;

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final Object browserLock = new Object();
    private Playwright playwright;
    private Browser browser;

    public LiveStatus resolve(String broadcastLink) {
        if (!StringUtils.hasText(broadcastLink)) {
            log.info("[LIVE-CHECK] skip: empty soop link -> offline");
            return LiveStatus.offline();
        }
        String normalized = normalizeLink(broadcastLink);
        long now = System.currentTimeMillis();
        CacheEntry cached = cache.get(normalized);
        if (cached != null && cached.expiresAt > now) {
            log.info(
                    "[LIVE-CHECK] 캐시결과={} link={} -> isLive={}, roomId={}, ttlMsLeft={}",
                    toKoreanStatus(cached.status),
                    normalized,
                    cached.status.isLive(),
                    cached.status.liveRoomId(),
                    cached.expiresAt - now);
            return cached.status;
        }

        LiveStatus resolved = fetchLiveStatus(normalized);
        cache.put(normalized, new CacheEntry(resolved, now + CACHE_TTL_MILLIS));
        log.info(
                "[LIVE-CHECK] 최종판정={} link={} -> isLive={}, roomId={}, cacheTtlMs={}",
                toKoreanStatus(resolved),
                normalized,
                resolved.isLive(),
                resolved.liveRoomId(),
                CACHE_TTL_MILLIS);
        return resolved;
    }

    public LiveStatus getCachedStatus(String broadcastLink) {
        if (!StringUtils.hasText(broadcastLink)) {
            return LiveStatus.offline();
        }
        CacheEntry cached = cache.get(normalizeLink(broadcastLink));
        return cached == null ? LiveStatus.offline() : cached.status();
    }

    private LiveStatus fetchLiveStatus(String link) {
        synchronized (browserLock) {
            URI sourceUri = URI.create(link);
            if (!isSoopHost(sourceUri)) {
                log.info("[LIVE-CHECK] non-soop host link={} -> offline", link);
                return LiveStatus.offline();
            }

            BrowserContext context = null;
            Page page = null;
            try {
                context = getBrowser().newContext();
                page = context.newPage();
                page.navigate(link, new Page.NavigateOptions().setTimeout(NAVIGATE_TIMEOUT_MS));
                page.waitForTimeout(JS_SETTLE_WAIT_MS);

                String finalUrl = page.url();
                String html = page.content();
                LiveStatus status = toLiveStatus(sourceUri, URI.create(finalUrl), html);
                log.info(
                        "[LIVE-CHECK] 판정={} source={} final={} status={} roomId={}",
                        toKoreanStatus(status),
                        sourceUri,
                        finalUrl,
                        status.isLive(),
                        status.liveRoomId());
                return status;
            } catch (Exception e) {
                log.warn("[LIVE-CHECK] 판정실패=방송종료 link={} reason={}", link, e.toString());
                return LiveStatus.offline();
            } finally {
                closeQuietly(page);
                closeQuietly(context);
            }
        }
    }

    private Browser getBrowser() {
        synchronized (browserLock) {
            if (browser != null && browser.isConnected()) {
                return browser;
            }
            if (playwright == null) {
                playwright = Playwright.create();
            }
            browser =
                    playwright
                            .chromium()
                            .launch(
                                    new BrowserType.LaunchOptions()
                                            .setHeadless(true)
                                            .setArgs(Arrays.asList("--disable-dev-shm-usage", "--no-sandbox")));
            log.info("[LIVE-CHECK] headless chromium launched");
            return browser;
        }
    }

    private static LiveStatus toLiveStatus(URI sourceUri, URI finalUri, String body) {
        if (sourceUri == null || finalUri == null || !isSoopHost(finalUri)) {
            return LiveStatus.offline();
        }
        String bjId = extractBjId(sourceUri.getPath());
        if (!StringUtils.hasText(bjId)) {
            return LiveStatus.offline();
        }
        LiveStatus fromFinalUri = parseLiveFromPath(finalUri.getPath(), bjId);
        if (fromFinalUri != null) {
            return fromFinalUri;
        }

        String html = body == null ? "" : body;
        LiveStatus fromHtml = parseLiveFromBody(html, bjId);
        if (fromHtml != null) {
            return fromHtml;
        }
        return LiveStatus.offline();
    }

    private static LiveStatus parseLiveFromPath(String path, String bjId) {
        if (!StringUtils.hasText(path)) {
            return null;
        }
        Matcher matcher = ROOM_PATH_PATTERN.matcher(path.trim());
        if (!matcher.matches()) {
            return null;
        }
        String pathBjId = matcher.group(1);
        String roomToken = matcher.group(2);
        if (!bjId.equalsIgnoreCase(pathBjId)) {
            return null;
        }
        if ("null".equalsIgnoreCase(roomToken)) {
            return LiveStatus.offline();
        }
        if (roomToken.matches("\\d+")) {
            return new LiveStatus(true, roomToken);
        }
        return LiveStatus.offline();
    }

    private static LiveStatus parseLiveFromBody(String html, String bjId) {
        if (!StringUtils.hasText(html)) {
            return null;
        }
        // 오프라인 안내 문구가 보이면 우선 오프라인으로 확정한다.
        if (html.contains("스트리머가 오프라인입니다.")) {
            return LiveStatus.offline();
        }
        Pattern p = Pattern.compile("/" + Pattern.quote(bjId) + "/(\\d+|null)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        if (!m.find()) {
            return null;
        }
        String token = m.group(1);
        if ("null".equalsIgnoreCase(token)) {
            return LiveStatus.offline();
        }
        return new LiveStatus(true, token);
    }

    private static String extractBjId(String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }
        String trimmed = path.trim();
        if (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1);
        }
        if (!StringUtils.hasText(trimmed)) {
            return null;
        }
        String[] parts = trimmed.split("/");
        return parts.length > 0 ? parts[0].trim() : null;
    }

    private static boolean isSoopHost(URI uri) {
        String host = uri.getHost();
        return StringUtils.hasText(host) && host.toLowerCase().contains("sooplive.com");
    }

    private static String normalizeLink(String link) {
        String trimmed = link.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        return "https://" + trimmed;
    }

    private static void closeQuietly(Page page) {
        try {
            if (page != null) {
                page.close();
            }
        } catch (Exception ignored) {
            // ignore close errors from already-disposed Playwright objects
        }
    }

    private static void closeQuietly(BrowserContext context) {
        try {
            if (context != null) {
                context.close();
            }
        } catch (Exception ignored) {
            // ignore close errors from already-disposed Playwright objects
        }
    }

    public record LiveStatus(boolean isLive, String liveRoomId) {
        public static LiveStatus offline() {
            return new LiveStatus(false, null);
        }
    }

    private static String toKoreanStatus(LiveStatus status) {
        return status != null && status.isLive() ? "방송중" : "방송종료";
    }

    private record CacheEntry(LiveStatus status, long expiresAt) {}

    @Override
    public void destroy() {
        synchronized (browserLock) {
            try {
                if (browser != null) {
                    browser.close();
                }
            } catch (Exception ignored) {
                // ignore
            } finally {
                browser = null;
            }
            try {
                if (playwright != null) {
                    playwright.close();
                }
            } catch (Exception ignored) {
                // ignore
            } finally {
                playwright = null;
            }
        }
    }
}
