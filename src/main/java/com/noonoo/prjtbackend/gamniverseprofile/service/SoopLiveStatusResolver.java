package com.noonoo.prjtbackend.gamniverseprofile.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.noonoo.prjtbackend.gamniverseprofile.config.SoopLiveStatusProperties;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class SoopLiveStatusResolver {

    private static final Pattern ROOM_PATH_PATTERN = Pattern.compile("^/([^/]+)/([^/]+)$");
    private static final String USER_AGENT = "GamniverseLiveCheck/1.0 (+https://gamniverse.local)";

    private final SoopLiveStatusProperties soopLiveStatusProperties;
    private final HttpClient httpClient =
            HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

    private Cache<String, LiveStatus> cache;
    private final ConcurrentHashMap<String, CompletableFuture<LiveStatus>> inflight = new ConcurrentHashMap<>();

    private Cache<String, LiveStatus> cache() {
        Cache<String, LiveStatus> c = cache;
        if (c == null) {
            synchronized (this) {
                c = cache;
                if (c == null) {
                    long ttlMs = Math.max(5_000L, soopLiveStatusProperties.getCacheTtlMs());
                    c =
                            Caffeine.newBuilder()
                                    .maximumSize(500)
                                    .expireAfterWrite(Duration.ofMillis(ttlMs))
                                    .build();
                    cache = c;
                }
            }
        }
        return c;
    }

    /**
     * 요청 시에만 SOOP를 조회합니다. TTL({@link SoopLiveStatusProperties#getCacheTtlMs()}) 동안 캐시를
     * 반환하며, 이후 요청이 없으면 추가 조회는 없습니다.
     */
    public LiveStatus resolve(String broadcastLink) {
        if (!StringUtils.hasText(broadcastLink)) {
            return LiveStatus.offline();
        }
        String normalized = normalizeLink(broadcastLink);
        if (!isAllowedSoopLink(normalized)) {
            log.warn("[LIVE-CHECK] blocked link host link={}", normalized);
            return LiveStatus.offline();
        }

        LiveStatus cached = cache().getIfPresent(normalized);
        if (cached != null) {
            return cached;
        }

        CompletableFuture<LiveStatus> pending = new CompletableFuture<>();
        CompletableFuture<LiveStatus> existing = inflight.putIfAbsent(normalized, pending);
        if (existing != null) {
            return joinQuietly(existing);
        }

        try {
            LiveStatus resolved = fetchLiveStatusHttp(normalized);
            cache().put(normalized, resolved);
            pending.complete(resolved);
            log.info(
                    "[LIVE-CHECK] resolved link={} isLive={} roomId={} cacheTtlMs={}",
                    normalized,
                    resolved.isLive(),
                    resolved.liveRoomId(),
                    soopLiveStatusProperties.getCacheTtlMs());
            return resolved;
        } catch (Exception e) {
            pending.completeExceptionally(e);
            log.warn("[LIVE-CHECK] failed link={} reason={}", normalized, e.toString());
            LiveStatus offline = LiveStatus.offline();
            cache().put(normalized, offline);
            return offline;
        } finally {
            inflight.remove(normalized);
        }
    }

    public boolean isCacheFresh(String broadcastLink) {
        if (!StringUtils.hasText(broadcastLink)) {
            return true;
        }
        return cache().getIfPresent(normalizeLink(broadcastLink)) != null;
    }

    public LiveStatus getCachedStatus(String broadcastLink) {
        if (!StringUtils.hasText(broadcastLink)) {
            return LiveStatus.offline();
        }
        LiveStatus cached = cache().getIfPresent(normalizeLink(broadcastLink));
        return cached == null ? LiveStatus.offline() : cached;
    }

    private LiveStatus fetchLiveStatusHttp(String link) throws Exception {
        URI sourceUri = URI.create(link);
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(sourceUri)
                        .timeout(Duration.ofSeconds(8))
                        .header("User-Agent", USER_AGENT)
                        .header("Accept", "text/html,application/xhtml+xml")
                        .GET()
                        .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        URI finalUri = response.uri();
        String html = response.body() == null ? "" : response.body();
        return toLiveStatus(sourceUri, finalUri, html);
    }

    static boolean isAllowedSoopLink(String link) {
        try {
            URI uri = URI.create(link);
            String host = uri.getHost();
            if (!StringUtils.hasText(host)) {
                return false;
            }
            String lower = host.toLowerCase();
            return lower.endsWith("sooplive.com") || lower.endsWith("sooplive.co.kr");
        } catch (Exception e) {
            return false;
        }
    }

    public static String normalizeLink(String link) {
        String trimmed = link.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        return "https://" + trimmed;
    }

    private static LiveStatus joinQuietly(CompletableFuture<LiveStatus> future) {
        try {
            return future.join();
        } catch (Exception e) {
            return LiveStatus.offline();
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
        return StringUtils.hasText(host) && host.toLowerCase().contains("sooplive");
    }

    public record LiveStatus(boolean isLive, String liveRoomId) {
        public static LiveStatus offline() {
            return new LiveStatus(false, null);
        }
    }
}
