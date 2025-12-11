package com.sentryq.thunder.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
public class RedisConfigTest {

    // Lua Script load Test
    // RedisConfig load ->
    // classes = RedisConfig.class -> conetext
    @Autowired
    private RedisScript<Long> applyEventScript;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("")
    void script_load_success () {

        // 1. bean != null (Spring Context)
        assertThat(applyEventScript).isNotNull();
        // 2. file path check
        assertThat(applyEventScript.getScriptAsString()).contains("return 0");
        // 3. return Type == Long
        assertThat(applyEventScript.getResultType()).isEqualTo(Long.class);
    }

    @Test
    @DisplayName("StringRedisTemplate must register successfully.")
    void template_load_success() {
        assertThat(stringRedisTemplate).isNotNull();
    }

}
