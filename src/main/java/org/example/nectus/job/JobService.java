package org.example.nectus.job;

import lombok.RequiredArgsConstructor;
import org.example.nectus.common.security.SecurityUtils;
import org.example.nectus.job.dto.*;
import org.example.nectus.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;

    @Transactional
    public JobResponse createJob(CreateJobRequest request){
        User currentUser = SecurityUtils.getCurrentUser();

        Job job = Job.builder()
                .poster(currentUser)
                .title(request.title())
                .company(request.company())
                .location(request.location())
                .description(request.description())
                .requirements(request.requirements())
                .salaryRange(request.salaryRange())
                .jobType(request.jobType())
                .status(JobStatus.OPEN)
                .build();

        jobRepository.save(job);
        return mapToJobResponse(job);
    }

    public PageResponse<JobResponse> getAllJobs(int page, int size){
        PageRequest pageRequest = PageRequest.of(
                page, size, Sort.by("createdAt").descending()
        );

        Page<Job> jobPage = jobRepository.findByStatus(JobStatus.OPEN, pageRequest);
        return mapToPageResponse(jobPage);
    }

    public PageResponse<JobResponse> searchJobs(String keyword, int page, int size){
        PageRequest pageRequest = PageRequest.of(
                page, size, Sort.by("createdAt").descending()
        );

        Page<Job> jobPage = jobRepository.searchJobs(keyword, pageRequest);
        return mapToPageResponse(jobPage);
    }

    public JobResponse getJob(UUID jobId){
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return mapToJobResponse(job);
    }

    @Transactional
    public JobResponse closeJob(UUID jobId){
        User currentUser = SecurityUtils.getCurrentUser();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPoster().getId().equals(currentUser.getId())){
            throw new RuntimeException("Not authorized to close this job");
        }

        job.setStatus(JobStatus.CLOSED);
        jobRepository.save(job);
        return mapToJobResponse(job);
    }

    @Transactional
    public ApplicationResponse applyToJob(UUID jobId, ApplyJobRequest request){
        User currentUser = SecurityUtils.getCurrentUser();
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() != JobStatus.OPEN){
            throw new RuntimeException("This job is no longer accepting applications");
        }

        if(job.getPoster().getId().equals(currentUser.getId())){
            throw new RuntimeException("You cannot apply to your own job posting");
        }

        if(applicationRepository.existsByJobIdAndApplicant_Id(jobId, currentUser.getId())){
            throw new RuntimeException("You have already applied to this job");
        }

        JobApplication application = JobApplication.builder()
                .job(job)
                .applicant(currentUser)
                .coverLetter(request.coverLetter())
                .status(ApplicationStatus.APPLIED)
                .build();

        applicationRepository.save(application);
        return mapToApplicationResponse(application);
    }

    public List<ApplicationResponse> getJobApplications(UUID jobId){
        User currentUser = SecurityUtils.getCurrentUser();

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPoster().getId().equals(currentUser.getId())){
            throw new RuntimeException("Not authorized to view these applications");
        }

        return applicationRepository.findByJobId(jobId)
                .stream()
                .map(this::mapToApplicationResponse)
                .toList();

    }

    public List<ApplicationResponse> getMyApplications(){
        User currentUser = SecurityUtils.getCurrentUser();
        return applicationRepository.findByApplicantId(currentUser.getId())
                .stream()
                .map(this::mapToApplicationResponse)
                .toList();
    }

    @Transactional
    public ApplicationResponse updateApplicationStatus(UUID applicationId, ApplicationStatus newStatus){
        User currentUser = SecurityUtils.getCurrentUser();
         JobApplication application = applicationRepository.findById(applicationId)
                 .orElseThrow(() -> new RuntimeException("Application not found"));

         if (!application.getJob().getPoster().getId().equals(currentUser.getId())){
             throw new RuntimeException("Not authorized to update this application");
         }

         application.setStatus(newStatus);
         applicationRepository.save(application);
         return mapToApplicationResponse(application);
    }

    private JobResponse mapToJobResponse(Job job){
        return new JobResponse(
                job.getId(),
                job.getPoster().getId(),
                job.getPoster().getFullName(),
                job.getTitle(),
                job.getTitle(),
                job.getCompany(),
                job.getLocation(),
                job.getDescription(),
                job.getRequirements(),
                job.getSalaryRange(),
                job.getStatus(),
                job.getCreatedAt()
        );
    }

    private ApplicationResponse mapToApplicationResponse(JobApplication app){
        return new ApplicationResponse(
                app.getId(),
                app.getJob().getId(),
                app.getJob().getTitle(),
                app.getJob().getCompany(),
                app.getApplicant().getId(),
                app.getApplicant().getFullName(),
                app.getCoverLetter(),
                app.getStatus(),
                app.getAppliedAt()
        );
    }

    private PageResponse<JobResponse> mapToPageResponse(Page<Job> page){
        List<JobResponse> content = page.getContent()
                .stream()
                .map(this::mapToJobResponse)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
