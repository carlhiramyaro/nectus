package org.example.nectus.user.dto;

import org.example.nectus.user.education.EducationDto;
import org.example.nectus.user.experience.WorkExperienceDto;
import org.example.nectus.user.project.ProjectDto;
import org.example.nectus.user.skill.SkillDto;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String fullName,
        String email,
        String headline,
        String bio,
        String location,
        String profilePictureUrl,
        List<WorkExperienceDto> workExperiences,
        List<EducationDto> educations,
        List<ProjectDto> projects,
        List<SkillDto> skills
) {
}
