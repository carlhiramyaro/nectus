package org.example.nectus.user;

import lombok.RequiredArgsConstructor;
import org.example.nectus.common.security.SecurityUtils;
import org.example.nectus.user.dto.*;
import org.example.nectus.user.education.Education;
import org.example.nectus.user.education.EducationDto;
import org.example.nectus.user.education.EducationRepository;
import org.example.nectus.user.experience.WorkExperience;
import org.example.nectus.user.experience.WorkExperienceDto;
import org.example.nectus.user.experience.WorkExperienceRepository;
import org.example.nectus.user.project.Project;
import org.example.nectus.user.project.ProjectDto;
import org.example.nectus.user.project.ProjectRepository;
import org.example.nectus.user.skill.Skill;
import org.example.nectus.user.skill.SkillDto;
import org.example.nectus.user.skill.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final EducationRepository educationRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;

    public UserProfileResponse getProfile(UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToProfileResponse(user);
    }

    public UserProfileResponse getMyProfile(){
        User user = SecurityUtils.getCurrentUser();
        return mapToProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request){
        User user = SecurityUtils.getCurrentUser();

        user.setFullName(request.fullName());
        user.setHeadline(request.headline());
        user.setBio(request.bio());
        user.setLocation(request.location());

        userRepository.save(user);
        return mapToProfileResponse(user);

    }

    @Transactional
    public WorkExperienceDto addWorkExperience(AddWorkExperienceRequest request){
        User user = SecurityUtils.getCurrentUser();

        WorkExperience experience = WorkExperience.builder()
                .user(user)
                .title(request.title())
                .company(request.company())
                .location(request.location())
                .description(request.description())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .currentRole(request.currentRole())
                .build();

        workExperienceRepository.save(experience);
        return mapToWorkExperienceDto(experience);
    }

    @Transactional
    public EducationDto addEducation(AddEducationRequest request){
        User user = SecurityUtils.getCurrentUser();

        Education education = Education.builder()
                .user(user)
                .institution(request.institution())
                .degree(request.degree())
                .fieldOfStudy(request.fieldOfStudy())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        educationRepository.save(education);
        return mapToEducationDto(education);
    }

    @Transactional
    public ProjectDto addProject(AddProjectRequest request){
        User user = SecurityUtils.getCurrentUser();

        Project project = Project.builder()
                .user(user)
                .title(request.title())
                .description(request.description())
                .url(request.url())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        projectRepository.save(project);
        return mapToProjectDto(project);
    }

    @Transactional
    public SkillDto addSkill(AddSkillRequest request){
        User user = SecurityUtils.getCurrentUser();

        Skill skill = Skill.builder()
                .user(user)
                .name(request.name())
                .build();

        skillRepository.save(skill);
        return mapToSkillDto(skill);
    }

    @Transactional
    public void deleteWorkExperience(UUID experienceId){
        User user = SecurityUtils.getCurrentUser();

        WorkExperience experience = workExperienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        if(!experience.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Not authorized to delete this entry");
        }

        workExperienceRepository.delete(experience);
    }

    @Transactional
    public void deleteEducation(UUID educationId){
        User user = SecurityUtils.getCurrentUser();

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if(!education.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Not authorized to delete this entry");
        }

        educationRepository.delete(education);
    }

    @Transactional
    public void deleteProject(UUID projectId){
        User user = SecurityUtils.getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if(!project.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Not authorized to delete this entry");
        }

        projectRepository.delete(project);
    }

    @Transactional
    public void deleteSkill(UUID skillId){
        User user = SecurityUtils.getCurrentUser();

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if(!skill.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Not authorized to delete this entry");
        }

        skillRepository.delete(skill);
    }

    private UserProfileResponse mapToProfileResponse(User user){
        List<WorkExperienceDto> experiences = workExperienceRepository
                .findByUserIdOrderByStartDateDesc(user.getId())
                .stream().map(this::mapToWorkExperienceDto).toList();

        List<EducationDto> educations = educationRepository
                .findByUserIdOrderByStartDateDesc(user.getId())
                .stream().map(this::mapToEducationDto).toList();

        List<ProjectDto> projects = projectRepository
                .findByUserId(user.getId())
                .stream().map(this::mapToProjectDto).toList();

        List<SkillDto> skills = skillRepository
                .findByUserId(user.getId())
                .stream().map(this::mapToSkillDto).toList();

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getHeadline(),
                user.getBio(),
                user.getLocation(),
                user.getProfilePictureUrl(),
                experiences,
                educations,
                projects,
                skills
        );

    }

    private WorkExperienceDto mapToWorkExperienceDto(WorkExperience exp){
        return new WorkExperienceDto(
                exp.getId(), exp.getTitle(), exp.getCompany(),
                exp.getLocation(), exp.getDescription(),
                exp.getStartDate(), exp.getEndDate(), exp.isCurrentRole()
        );
    }

    private EducationDto mapToEducationDto(Education edu){
        return new EducationDto(
                edu.getId(), edu.getInstitution(), edu.getDegree(),
                edu.getFieldOfStudy(), edu.getStartDate(),
                edu.getEndDate(), edu.getDescription()
        );
    }

    private ProjectDto mapToProjectDto(Project proj){
        return new ProjectDto(
                proj.getId(), proj.getTitle(), proj.getDescription(),
                proj.getUrl(), proj.getStartDate(), proj.getEndDate()
        );
    }

    private SkillDto mapToSkillDto(Skill skill){
        return new SkillDto(skill.getId(), skill.getName());
    }


}
