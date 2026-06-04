package org.example.nectus.job;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    @Query("""
SELECT j FROM Job j
WHERE j.status = 'OPEN'
AND (Lower(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
OR LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
    Page<Job> searchJobs(@Param("keyword") String keyword, Pageable pageable);

    Page<Job> findByPosterId(UUID posterId, Pageable pageable);
}
