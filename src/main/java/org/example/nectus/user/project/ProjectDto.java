package org.example.nectus.user.project;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectDto(
        UUID id,
        String title,
        String description,
        String url,
        LocalDate startDate,
        LocalDate endDate
) {
}
