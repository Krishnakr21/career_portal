package com.service_user.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.service_user.user_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.service_user.user_service.utility.JwtUtil;
import com.service_user.user_service.model.User;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    
    @Autowired
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public User getUserDetails(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Map<String, String> getRole(String email) {
        User user = getUserDetails(email);
        Map<String, String> response = new HashMap<>();
        response.put("role", user.getRole());
        return response;
    }

    public Map<String, String> removeUser(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    
        userRepository.deleteByEmail(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User removed successfully");
        return response;
    }

    public List<User> getAllUsers(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        String email = jwtUtil.extractEmail(token);
        User user = getUserDetails(email);
        if (!user.getRole().equals("ADMIN") && !user.getRole().equals("SUPER_ADMIN")) {
            throw new RuntimeException("User is not an admin or super admin");
        }
        return userRepository.findAll();
    }
}
