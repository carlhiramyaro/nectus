package org.example.nectus.job.dto;

import org.example.nectus.job.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationResponse(
        UUID applicationId,
        UUID jobId,
        String jobTitle,
        String company,
        UUID applicantId,
        String applicantName,
        String coverLetter,
        ApplicationStatus status,
        LocalDateTime appliedAt
) {
}
