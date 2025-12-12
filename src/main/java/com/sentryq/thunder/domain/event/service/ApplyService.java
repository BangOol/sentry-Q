package com.sentryq.thunder.domain.event.service;


import com.sentryq.thunder.domain.event.constant.ApplyResult;
import com.sentryq.thunder.global.config.RedisKeyHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyService {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> applyEventScript;

    public boolean apply(Long eventId, Long userId) {
        // 1. Key 설정 (hash Tag 적용)
        String stockKey = RedisKeyHelper.getStockKey(eventId);
        String applySetKey = RedisKeyHelper.getStockKey(eventId);

        // 2. Lua Script 실행
        Long result = redisTemplate.execute(
                applyEventScript,
                List.of(applySetKey, stockKey),
                String.valueOf(userId)
        );

        // 3. 결과 처리
        return switch (result.intValue()) {
            case 0 -> {
                log.info("Apply SUCCESS");
                yield ApplyResult.SUCCESS; // Enum 값 반환
            }
            case -1 -> {
                log.warn("Apply FAILED: SOLD_OUT");
                yield ApplyResult.SOLD_OUT; // Enum 값 반환
            }
            case -2 -> {
                log.warn("Apply FAILED: DUPLICATE");
                yield ApplyResult.DUPLICATE; // Enum 값 반환
            }
            default -> {
                // 예상치 못한 Redis 코드가 들어온 경우, 시스템 오류로 처리
                log.error("Unknown Redis result code: {}", result.intValue());
                throw new IllegalStateException("Unknown result code from Redis: " + result.intValue());
            }
        };
    }
}
