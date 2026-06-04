package org.example.nectus.job;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.nectus.job.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody CreateJobRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(request));

    }

    @GetMapping
    public ResponseEntity<PageResponse<JobResponse>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(jobService.getAllJobs(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<JobResponse>> searchJobs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(jobService.searchJobs(keyword, page, size));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(@PathVariable UUID jobId){
        return ResponseEntity.ok(jobService.getJob(jobId));
    }

    @PatchMapping("/{jobId}/close")
    public ResponseEntity<JobResponse> closeJob(@PathVariable UUID jobId){
        return ResponseEntity.ok(jobService.closeJob(jobId));
    }

    @PostMapping("/{jobId}/apply")
    public ResponseEntity<ApplicationResponse> applyToJob(
            @PathVariable UUID jobId,
            @RequestBody ApplyJobRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.applyToJob(jobId, request));
    }

    @GetMapping("/{jobId}/applications")
    public ResponseEntity<List<ApplicationResponse>> getJobApplications(
            @PathVariable UUID jobId
    ){
        return ResponseEntity.ok(jobService.getJobApplications(jobId));
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplication(){
        return ResponseEntity.ok(jobService.getMyApplications());
    }

    @PatchMapping("/applications/{applicationId}/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable UUID applicationId,
            @RequestParam ApplicationStatus status
    ){
        return ResponseEntity.ok(jobService.updateApplicationStatus(applicationId, status));
    }

}
