package org.example.nectus.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank String fullName,
        String headline,
        String bio,
        String location
        ) {
}
