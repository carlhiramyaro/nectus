package org.example.nectus.user.experience;

import java.time.LocalDate;
import java.util.UUID;

public record WorkExperienceDto(
        UUID id,
        String title,
        String company,
        String location,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        boolean currentRole
) {
}
