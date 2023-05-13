package com.example.springsecurity.user.dto;

public class UserRoleWithEmailDto {
    private final String email;
    private final boolean isAdmin;

    public UserRoleWithEmailDto(String email, boolean isAdmin) {
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
