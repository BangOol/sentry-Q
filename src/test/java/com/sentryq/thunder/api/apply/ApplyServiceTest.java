package com.sentryq.thunder.api.apply;

import com.sentryq.thunder.domain.event.service.ApplyService;
import com.sentryq.thunder.global.config.RedisKeyHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class ApplyServiceTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final Long EVENT_ID = 100L;
    private final Long USER_ID = 1234L;
    private String stockKey;
    private String applySetKey;

    @BeforeEach
    void setUp() {
        // redis key 초기화.
        stockKey = RedisKeyHelper.getStockKey(EVENT_ID);
        applySetKey = RedisKeyHelper.getApplySetKey(EVENT_ID);

        redisTemplate.delete(stockKey);
        redisTemplate.delete(applySetKey);
    }

    @Test
    @DisplayName("성공 : 재고가 충분하고, 중복이 아니면 true 반환")
    void apply_success() {
        // given
        redisTemplate.opsForValue().set(stockKey, "100");

        // when
        boolean result = applyService.apply(EVENT_ID, USER_ID);

        // then
        assertThat(result).isTrue();

        // 재고 차감 확인 (100 -> 99)
        String remainingStock = redisTemplate.opsForValue().get(stockKey);
        assertThat(remainingStock).isEqualTo("99");

        // 유저 등록 확인
        Boolean isMember = redisTemplate.opsForSet().isMember(applySetKey, String.valueOf(USER_ID));
        assertThat(isMember).isTrue();
    }

    @Test
    @DisplayName("실패 : 이미 신청한 유저는 false를 반환 (중복 방지)")
    void apply_fail_duplicate() {
        // given
        redisTemplate.opsForValue().set(stockKey, "100");
        applyService.apply(EVENT_ID, USER_ID);

        // when
        boolean result = applyService.apply(EVENT_ID, USER_ID);

        // then
        assertThat(result).isTrue();

        // 재고가 추가로 차감되지 않아야 한다.
        String remainingStock = redisTemplate.opsForValue().get(stockKey);
        assertThat(remainingStock).isEqualTo("99");
    }

    @Test
    @DisplayName("실패 : 재고 부족 시 false 반환 (품절 처리)")
    void apply_fail_sold_out() {
        // given
        redisTemplate.opsForValue().set(stockKey, "0");

        // when
        boolean result = applyService.apply(EVENT_ID, USER_ID);

        // then
        assertThat(result).isFalse();

        // 유저가 Set에 등록되지 않아야 한다.
        Boolean isMember = redisTemplate.opsForSet().isMember(applySetKey, String.valueOf(USER_ID));
        assertThat(isMember).isFalse();
    }
}
