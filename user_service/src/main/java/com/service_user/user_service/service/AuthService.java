package com.service_user.user_service.service;

import com.service_user.user_service.utility.JwtUtil;
import com.service_user.user_service.dto.AuthResponse;
import com.service_user.user_service.dto.LoginRequest;
import com.service_user.user_service.dto.SignupRequest;
import com.service_user.user_service.exception.EmailAlreadyExistsException;
import com.service_user.user_service.model.User;
import com.service_user.user_service.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${verification-url}")
    private String verificationUrl;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      JwtUtil jwtUtil, AuthenticationManager authenticationManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public Map<String, String> signup(SignupRequest request) {
        User existingUser = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (existingUser != null) {
            if (existingUser.isVerified()) {
                throw new EmailAlreadyExistsException("Email already registered");
            } else {
                // Resend verification email if the user exists but is not verified
                String verificationToken = jwtUtil.generateVerificationToken(existingUser.getEmail());
                String verificationLink = verificationUrl + verificationToken;
                emailService.sendVerificationEmail(existingUser.getEmail(), verificationLink);
                return Map.of("message", "Email already registered but not verified. Verification email resent.");
            }
        }

        // Create a new user if the email is not registered
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            user.setRole(request.getRole());
        }
        userRepository.save(user);

        // Send verification email for the new user
        String verificationToken = jwtUtil.generateVerificationToken(user.getEmail());
        String verificationLink = verificationUrl + verificationToken;
        emailService.sendVerificationEmail(user.getEmail(), verificationLink);

        return Map.of("message", "Signup successful. Please check your email to verify your account.");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        if (!user.isVerified()) {
            // Resend verification email if the user is not verified
            String verificationToken = jwtUtil.generateVerificationToken(user.getEmail());
            String verificationLink = verificationUrl + verificationToken;
            emailService.sendVerificationEmail(user.getEmail(), verificationLink);
            throw new RuntimeException("Email not verified. A new verification email has been sent. Please verify your email before logging in.");
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        return generateTokens(authentication.getName());
    }

    private AuthResponse generateTokens(String email) {
        return new AuthResponse(
            jwtUtil.generateAccessToken(email),
            jwtUtil.generateRefreshToken(email)
        );
    }

    public String getUserRole(String email) {
        return userRepository.findByEmail(email)
                .map(User::getRole)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public Map<String, String> removeUser(String email) {
        int deleted = userRepository.deleteByEmail(email);
        if (deleted == 0) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return Map.of("message", "User removed successfully");
    }

    public Map<String, String> verifyEmail(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Invalid token");
            }
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            user.setVerified(true);
            userRepository.save(user);
            return Map.of("message", "Email verified successfully");
        } catch (Exception e) {
            // Log the exception for debugging
            System.err.println("Error during email verification: " + e.getMessage());
            throw new RuntimeException("Failed to verify email: " + e.getMessage());
        }
    }

    public Map<String, String> resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String verificationToken = jwtUtil.generateVerificationToken(user.getEmail());
        String verificationLink = verificationUrl + verificationToken;
        emailService.sendVerificationEmail(user.getEmail(), verificationLink);
        return Map.of("message", "Verification email sent. Please check your email.");
    }
}