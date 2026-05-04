package org.example.nectus.auth.dto;

import jakarta.validation.constraints.*;


public record RegisterRequest (
    @NotBlank(message = "Full name is required")
    String fullName,

    @Email(message = "Must be a valid email")
    @NotBlank(message = "Email is required")
    String email,

    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password is required")
    String password
){}




