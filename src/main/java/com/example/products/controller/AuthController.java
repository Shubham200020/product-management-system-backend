package com.example.products.controller;

import com.example.products.dto.LoginRequest;
import com.example.products.dto.RegisterRequest;
import com.example.products.model.User;
import com.example.products.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    @Autowired
    AuthService authService;

    @Value("${app.jwt.cookie-name}")
    private String jwtCookie;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        authService.registerAdmin(request);
        return ResponseEntity.ok(Map.of("message", "Admin registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        String jwt = authService.login(request);
        User user = authService.getUserByIdentifier(request.getIdentifier());
        
        // Security Cookie (HttpOnly)
        Cookie cookie = new Cookie(jwtCookie, jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        // User Info Cookie (Visible to Angular) - URL Encoded to handle spaces
        String userInfo = java.net.URLEncoder.encode(user.getName() + ":" + user.getRole(), java.nio.charset.StandardCharsets.UTF_8);
        Cookie userCookie = new Cookie("user-info", userInfo);
        userCookie.setHttpOnly(false);
        userCookie.setPath("/");
        userCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(userCookie);
        
        return ResponseEntity.ok(Map.of(
            "message", "Login successful",
            "name", user.getName(),
            "role", user.getRole()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(jwtCookie, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        Cookie userCookie = new Cookie("user-info", null);
        userCookie.setPath("/");
        userCookie.setMaxAge(0);
        response.addCookie(userCookie);
        
        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody com.example.products.dto.ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password reset successful. You can now login with your new password."));
    }
}
