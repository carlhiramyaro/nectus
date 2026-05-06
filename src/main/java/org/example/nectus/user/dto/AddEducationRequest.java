package org.example.nectus.user.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record AddEducationRequest(
        @NotBlank String institution,
        @NotBlank  String degree,
        String fieldOfStudy,
        @NotBlank LocalDate startDate,
        LocalDate endDate,
        String description
) {
}
