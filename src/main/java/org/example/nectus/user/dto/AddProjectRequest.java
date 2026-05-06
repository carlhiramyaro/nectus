package org.example.nectus.user.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record AddProjectRequest(
        @NotBlank String title,
        @NotBlank String description,
        String url,
        LocalDate startDate,
        LocalDate endDate
) {
}
