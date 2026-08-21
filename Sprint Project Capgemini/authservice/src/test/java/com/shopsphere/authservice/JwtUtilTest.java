package com.shopsphere.authservice;

import com.shopsphere.authservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Teri yml wali secret key yahan manually set kar rahe hain test ke liye
        ReflectionTestUtils.setField(jwtUtil, "secret", "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
    }

    @Test
    void generateAndValidateToken() {
        String email = "vir@test.com";
        String role = "ADMIN";

        String token = jwtUtil.generateToken(email, role);

        assertNotNull(token);
        assertEquals(email, jwtUtil.extractEmail(token));
        assertTrue(jwtUtil.validateToken(token), email);
    }
}