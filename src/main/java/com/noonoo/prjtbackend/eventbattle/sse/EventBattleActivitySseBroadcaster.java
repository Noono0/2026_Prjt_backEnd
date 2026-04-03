package com.noonoo.prjtbackend.eventbattle.sse;

import com.noonoo.prjtbackend.eventbattle.dto.EventBattleActivityDto;
import com.noonoo.prjtbackend.eventbattle.service.EventBattleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Objects;

/**
 * eventBattleSeq 별 SSE 구독자 목록을 보관하고, 베팅/정산/취소 시 activity 스냅샷을 푸시합니다.
 *
 * 주의: 연결을 많이 열 수 있으므로, heartbeat은 최소 빈도로만 보냅니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventBattleActivitySseBroadcaster {

    private static final int ACTIVITY_RECENT_LIMIT = 20;
    private static final long HEARTBEAT_INTERVAL_SECONDS = 25;

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emittersBySeq = new ConcurrentHashMap<>();

    /**
     * EventBattleServiceImpl가 이 브로드캐스터를 참조하므로, 서비스 빈을 생성자에서 직접 주입하면 순환이 납니다.
     * {@link ObjectProvider}는 빈 생성 시점에 구현체를 만들지 않아 순환을 끊습니다.
     */
    private final ObjectProvider<EventBattleService> eventBattleService;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private EventBattleService svc() {
        return eventBattleService.getObject();
    }

    public SseEmitter subscribe(long eventBattleSeq) {
        SseEmitter emitter = new SseEmitter(0L); // timeout 없음(프록시/브라우저가 관리)
        emittersBySeq.computeIfAbsent(eventBattleSeq, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(eventBattleSeq, emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            remove(eventBattleSeq, emitter);
        });

        // 연결 즉시 첫 스냅샷
        try {
            EventBattleActivityDto a = svc().activity(eventBattleSeq, null, ACTIVITY_RECENT_LIMIT);
            if (a == null) {
                throw new IllegalStateException("activity 스냅샷 생성 실패");
            }
            emitter.send(SseEmitter.event().name("init").data(a));
            if (a.getStatus() != null && !"OPEN".equals(a.getStatus())) {
                emitter.complete();
            }
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "SSE 스냅샷 생성 실패";
            try {
                emitter.send(SseEmitter.event().name("error").data(Objects.requireNonNull(msg)));
            } catch (IOException ignored) {
                // ignore
            }
            emitter.completeWithError(e);
            remove(eventBattleSeq, emitter);
        }

        return emitter;
    }

    public void broadcastActivity(long eventBattleSeq) {
        CopyOnWriteArrayList<SseEmitter> emitters = emittersBySeq.get(eventBattleSeq);
        if (emitters == null || emitters.isEmpty()) return;

        EventBattleActivityDto a = svc().activityForBroadcast(eventBattleSeq, ACTIVITY_RECENT_LIMIT);
        if (a == null) return;
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("activity").data(a));
                if (a.getStatus() != null && !"OPEN".equals(a.getStatus())) {
                    emitter.complete();
                    remove(eventBattleSeq, emitter);
                }
            } catch (IOException e) {
                emitter.complete();
                remove(eventBattleSeq, emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
                remove(eventBattleSeq, emitter);
            }
        }
    }

    @jakarta.annotation.PostConstruct
    void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                for (Map.Entry<Long, CopyOnWriteArrayList<SseEmitter>> entry : emittersBySeq.entrySet()) {
                    long seq = entry.getKey();
                    for (SseEmitter emitter : entry.getValue()) {
                        try {
                            emitter.send(SseEmitter.event().name("ping").data(""));
                        } catch (Exception e) {
                            try {
                                emitter.complete();
                            } catch (Exception ignored) {
                                // ignore
                            }
                            remove(seq, emitter);
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("SSE heartbeat failed: {}", e.getMessage());
            }
        }, HEARTBEAT_INTERVAL_SECONDS, HEARTBEAT_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    @jakarta.annotation.PreDestroy
    void stopHeartbeat() {
        scheduler.shutdownNow();
    }

    private void remove(long eventBattleSeq, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = emittersBySeq.get(eventBattleSeq);
        if (list == null) return;
        list.remove(emitter);
        if (list.isEmpty()) {
            emittersBySeq.remove(eventBattleSeq);
        }
    }
}

