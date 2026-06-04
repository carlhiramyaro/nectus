package org.example.nectus.job.dto;

import org.example.nectus.job.JobStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record JobResponse(
        UUID id,
        UUID posterId,
        String posterName,
        String title,
        String company,
        String location,
        String description,
        String requirements,
        String salaryRange,
        String jobType,
        JobStatus status,
        LocalDateTime createdAt
) {
}
