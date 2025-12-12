package com.sentryq.thunder.api.apply;


import com.sentryq.thunder.api.utils.RedisKeyHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

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
                List.of(stockKey, applySetKey),
                String.valueOf(userId)
        );

        // 3. 결과 처리
        if (result == null) {
            throw new IllegalStateException("Redis execution failed");
        }

        return switch (result.intValue()) {
            case 0 -> true;
            default -> false;
        };
    }
}
