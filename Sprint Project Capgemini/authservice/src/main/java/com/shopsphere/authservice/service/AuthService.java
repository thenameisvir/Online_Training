package com.shopsphere.authservice.service;

import com.shopsphere.authservice.dto.LoginRequest;
import com.shopsphere.authservice.dto.RegisterRequest;
import com.shopsphere.authservice.entity.User;
import com.shopsphere.authservice.repository.UserRepository;
import com.shopsphere.authservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.shopsphere.authservice.exception.InvalidCredentialsException;
import com.shopsphere.authservice.exception.UserAlreadyExistsException;


import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, String> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        return response;
    }

    public Map<String, String> login(LoginRequest request) {
        System.out.println("====== LOGIN ATTEMPT START ======");
        System.out.println("Email received from Frontend: " + request.getEmail());
        System.out.println("Password received from Frontend: " + request.getPassword());



        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    System.out.println("DEBUG: User not found in Database for email: " + request.getEmail());
                    return new InvalidCredentialsException("User not found!");
                });

        System.out.println("User found in DB: " + user.getEmail());
        System.out.println("Encoded Password in DB: " + user.getPassword());

        boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("Does password match? : " + isMatch);

        if (!isMatch) {
            System.out.println("DEBUG: Password mismatch for user: " + request.getEmail());
            throw new InvalidCredentialsException("Invalid password!");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        System.out.println("Token generated successfully!");
        System.out.println("====== LOGIN ATTEMPT END ======");

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole().name());
        response.put("name", user.getName());
        return response;


    }
}