package org.example.nectus.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AddWorkExperienceRequest(
        @NotBlank String title,
        @NotBlank String company,
        String location,
        String description,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        boolean currentRole

        ) {
}
