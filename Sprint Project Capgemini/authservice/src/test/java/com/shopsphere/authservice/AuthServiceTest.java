package com.shopsphere.authservice;

import com.shopsphere.authservice.dto.LoginRequest;
import com.shopsphere.authservice.dto.RegisterRequest;
import com.shopsphere.authservice.entity.User;
import com.shopsphere.authservice.exception.UserAlreadyExistsException;
import com.shopsphere.authservice.repository.UserRepository;
import com.shopsphere.authservice.security.JwtUtil;
import com.shopsphere.authservice.service.AuthService;
import com.shopsphere.authservice.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService authService;

    // Helper method to create a valid user
    private User createMockUser(User.Role role) {
        User user = new User();
        user.setName("Madhur");
        user.setEmail("madhur@gmail.com");
        user.setPassword("encodedPass");
        user.setRole(role);
        return user;
    }

    @Test
    void register_Success() {
        // Arrange
        RegisterRequest req = new RegisterRequest("Madhur", "madhur@gmail.com", "pass123", "CUSTOMER");
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encodedPass");

        // Act
        Map<String, String> res = authService.register(req);

        // Assert
        assertEquals("User registered successfully!", res.get("message"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_UserAlreadyExists_ThrowsException() {
        RegisterRequest req = new RegisterRequest("Madhur", "madhur@gmail.com", "pass123", "CUSTOMER");
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        // Specific Exception use karna hai
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        LoginRequest req = new LoginRequest("madhur@gmail.com", "pass123");
        User user = createMockUser(User.Role.CUSTOMER);

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken(user.getEmail(), "CUSTOMER")).thenReturn("mockToken");

        Map<String, String> res = authService.login(req);

        assertNotNull(res.get("token"));
        assertEquals("CUSTOMER", res.get("role"));
        assertEquals("Madhur", res.get("name"));
    }

    @Test
    void login_InvalidUser_ThrowsException() {
        LoginRequest req = new LoginRequest("wrong@gmail.com", "pass123");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        LoginRequest req = new LoginRequest("madhur@gmail.com", "wrongpass");
        User user = createMockUser(User.Role.CUSTOMER);

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encodedPass")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(req));
    }
}