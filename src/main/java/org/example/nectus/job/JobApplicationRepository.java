package org.example.nectus.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    List<JobApplication> findByJobId(UUID jobId);

    List<JobApplication> findByApplicantId(UUID applicantId);

    boolean existsByJobIdAndApplicant_Id(UUID jobId, UUID applicantId);

    Optional<JobApplication> findByJobIdAndApplicant_Id(UUID jobId, UUID applicantId);
}
