package com.example.products.controller;

import com.example.products.model.User;
import com.example.products.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(java.security.Principal principal) {
        return ResponseEntity.ok(userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(@RequestBody User userDetails, java.security.Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setName(userDetails.getName());
        user.setPhone(userDetails.getPhone());
        user.setEmail(userDetails.getEmail());
        user.setProfilePicture(userDetails.getProfilePicture());
        
        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email, @RequestParam(required = false) Long excludeId) {
        return userRepository.findByEmail(email)
                .map(u -> !u.getId().equals(excludeId))
                .orElse(false);
    }

    @GetMapping("/check-phone")
    public boolean checkPhone(@RequestParam String phone, @RequestParam(required = false) Long excludeId) {
        // Assuming we add findByPhone to repository if needed, or use stream
        return userRepository.findAll().stream()
                .anyMatch(u -> u.getPhone().equals(phone) && !u.getId().equals(excludeId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Long id, @RequestParam boolean active, java.security.Principal principal) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getEmail().equals(principal.getName())) {
            throw new RuntimeException("You cannot deactivate your own account");
        }

        user.setActive(active);
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createAdmin(@RequestBody User admin) {
        if (userRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole(com.example.products.model.Role.ADMIN);
        admin.setActive(true);
        return ResponseEntity.ok(userRepository.save(admin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, java.security.Principal principal) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEmail().equals(principal.getName())) {
            throw new RuntimeException("You cannot delete your own account");
        }

        userRepository.delete(user);
        return ResponseEntity.ok().build();
    }
}
