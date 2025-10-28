package com.service_user.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service_user.user_service.utility.JwtUtil;
import com.service_user.user_service.model.User;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import org.springframework.http.HttpStatus;
import com.service_user.user_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import com.service_user.user_service.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public UserController(JwtUtil jwtUtil, AuthService authService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserDetails(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserDetails(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/role")
    public ResponseEntity<Map<String, String>> getRole(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.getRole(jwtUtil.getEmailFromToken(token.replace("Bearer ", ""))));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, String>> removeUser(
            @RequestHeader("Authorization") String token,
            @RequestParam String email) {

        String requesterEmail = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
        String requesterRole = authService.getUserRole(requesterEmail);

        try {
            String targetRole = authService.getUserRole(email);

            if (requesterRole.equals("ADMIN") &&
                    !targetRole.equals("ADMIN") &&
                    !targetRole.equals("SUPER_ADMIN")) {
                return ResponseEntity.ok(authService.removeUser(email));
            } else if (requesterRole.equals("SUPER_ADMIN") &&
                    targetRole.equals("ADMIN")) {
                return ResponseEntity.ok(authService.removeUser(email));
            } else if (requesterRole.equals("ADMIN") &&
                    (targetRole.equals("ADMIN") || targetRole.equals("SUPER_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin cannot remove other admins"));
            } else if (requesterRole.equals("SUPER_ADMIN") && targetRole.equals("SUPER_ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Super admin cannot remove other super admins"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient privileges"));
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User with email " + email + " not found"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(userService.getAllUsers(token.replace("Bearer ", "")));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }
}
