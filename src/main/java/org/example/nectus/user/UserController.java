package org.example.nectus.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.nectus.common.security.SecurityUtils;
import org.example.nectus.user.dto.*;
import org.example.nectus.user.education.Education;
import org.example.nectus.user.education.EducationDto;
import org.example.nectus.user.experience.WorkExperienceDto;
import org.example.nectus.user.project.ProjectDto;
import org.example.nectus.user.skill.SkillDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

//    @GetMapping
//    public ResponseEntity<User> getCurrentUser(){
//        User user = SecurityUtils.getCurrentUser();
//        return ResponseEntity.ok(user);
//    }

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(){
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable UUID userId){
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request){
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @PostMapping("/me/experience")
    public ResponseEntity<WorkExperienceDto> addExperience(@Valid @RequestBody AddWorkExperienceRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addWorkExperience(request));
    }

    @DeleteMapping("/me/experience/{experienceId}")
    public ResponseEntity<Void> deleteExperience(@PathVariable UUID experienceId){
        userService.deleteWorkExperience(experienceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/education")
    public ResponseEntity<EducationDto> addEducation(@Valid @RequestBody AddEducationRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addEducation(request));
    }

    @DeleteMapping("/me/education/{educationId}")
    public ResponseEntity<Void> deleteEducation(@PathVariable UUID educationId){
        userService.deleteEducation(educationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/project")
    public ResponseEntity<ProjectDto> addProject(@Valid @RequestBody AddProjectRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addProject(request));
    }

    @DeleteMapping("/me/education/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId){
        userService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/skill")
    public ResponseEntity<SkillDto> addProject(@Valid @RequestBody AddSkillRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addSkill(request));
    }

    @DeleteMapping("/me/education/{skillId}")
    public ResponseEntity<Void> deleteSkill(@PathVariable UUID skillId){
        userService.deleteProject(skillId);
        return ResponseEntity.noContent().build();
    }



}
