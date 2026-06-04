package org.example.nectus.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.nectus.job.JobType;

public record CreateJobRequest(
        @NotBlank String title,
        @NotBlank String company,
        @NotBlank String location,
        @NotBlank String description,
        String requirements,
        String salaryRange,
        @NotNull JobType jobType
) {
}
