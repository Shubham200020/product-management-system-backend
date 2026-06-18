package com.example.products.dto;

public class LoginRequest {
    private String identifier; // email or phone
    private String password;

    public LoginRequest() {}

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        if (identifier != null) {
            this.identifier = identifier.contains("@") ? identifier.trim().toLowerCase() : identifier.trim();
        } else {
            this.identifier = null;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
