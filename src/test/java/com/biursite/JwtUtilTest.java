package com.biursite;

import com.biursite.security.JwtUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtUtilTest {

    @Test
    void generateAndValidateToken() throws Exception {
        JwtUtil jwtUtil = new JwtUtil();
        // configure fields via reflection for isolated unit test
        Field secretField = JwtUtil.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "test-secret-key-which-is-long-enough-1234567890");

        Field expField = JwtUtil.class.getDeclaredField("jwtExpirationMs");
        expField.setAccessible(true);
        expField.setLong(jwtUtil, 3600000L);

        String token = jwtUtil.generateToken("alice", "ROLE_USER");
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.getUsernameFromToken(token)).isEqualTo("alice");
    }
}
