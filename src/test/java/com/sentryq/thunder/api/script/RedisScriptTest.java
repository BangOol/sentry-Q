package com.sentryq.thunder.api.script;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisScriptTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private String stockKey;
    private String applySetKey;

    // 테스트할 Lua Script (추후 파일로 분리 예정)
    private final String luaScript =
            "if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then " +
            "   return -2 " + // Code: Already Applied
            "end " +
            "local stock = tonumber(redis.call('GET', KEYS[1]) or '0') " +
            "if stock <= 0 then " +
            "   return -1 " + // Code: Sold Out
            "end " +
            "redis.call('DECR', KEYS[1]) " +
            "redis.call('SADD', KEYS[2], ARGV[1]) " +
            "return 0"; // Code: Success


    @BeforeEach
    void setUp() {
        stockKey = "test:stock:1";
        applySetKey = "test:apply:users:1";

        redisTemplate.delete(stockKey);
        redisTemplate.delete(applySetKey);
    }

    @Test
    @DisplayName("성공 케이스 : 재고가 있고 중복이 아니면, 신청에 성공한다.")
    void script_success() {
        // given
        String userId = "user_100";
        redisTemplate.opsForValue().set(stockKey, "100");

        RedisScript<Long> script = RedisScript.of(luaScript, Long.class);

        // when
        Long result = redisTemplate.execute(script, List.of(stockKey, applySetKey), userId);

        // then
        assertThat(result).isEqualTo(0L);

        // 검증 1 : 재고가 99개로 줄었는가?
        String remainingStock = redisTemplate.opsForValue().get(stockKey);
        assertThat(remainingStock).isEqualTo("99");

        // 검증 2 : 유저가 Set에 등록되었는가?
        Boolean isMember = redisTemplate.opsForSet().isMember(applySetKey, userId);
        assertThat(isMember).isTrue();
    }
    

    @Test
    @DisplayName("중복 케이스 : 이미 신청한 유저는 실패 코드(-2)를 반환한다.")
    void script_duplicate_fail() {
        // given
        String userId = "user_100";
        redisTemplate.opsForValue().set(stockKey, "100");
        redisTemplate.opsForSet().add(applySetKey, userId); // 이미 신청;
        RedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);

        // when
        Long result = redisTemplate.execute(script, List.of(stockKey, applySetKey), userId);

        // then
        assertThat(result).isEqualTo(-2L);

        // 검증 : 재고가 차감되지 않았는가?
        // 이렇게 값을 꺼내게 될 때, 해당 값을 이미 Java 어플리케이션에 가지고 왔기 때문에 괜찮은건가?
        // 아니면 다시 Redis에서 값을 꺼내는건가?
        String stock = redisTemplate.opsForValue().get(stockKey);
        assertThat(stock).isEqualTo("100");
    }

    @Test
    @DisplayName("재고 없음 케이스 : 재고가 없으면 실패 코드(-1)를 반환한다.")
    void script_sold_out_fail() {
        // given
        String userId = "user_100";
        redisTemplate.opsForValue().set(stockKey, "0");

        RedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);

        // when
        Long result = redisTemplate.execute(script, List.of(stockKey, applySetKey), userId);

        // then
        assertThat(result).isEqualTo(-1L);

        // 검증 : 유저가 set에 추가되지 않는가?
        Boolean isMember = redisTemplate.opsForSet().isMember(applySetKey, userId);
        assertThat(isMember).isFalse();
    }
}
