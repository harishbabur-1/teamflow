
package com.teamflow.project.controller;

import com.teamflow.project.dto.AddProjectMemberRequest;
import com.teamflow.project.dto.CreateProjectRequest;
import com.teamflow.project.dto.ProjectMemberResponse;
import com.teamflow.project.dto.ProjectResponse;
import com.teamflow.project.entity.Project;
import com.teamflow.project.entity.ProjectMember;
import com.teamflow.project.service.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponse createProject(
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication) {

        Project project = projectService.createProject(
                request, authentication);

        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setWorkspaceId(project.getWorkspace().getId());
        response.setCreatedBy(project.getCreatedBy().getEmail());
        response.setCreatedAt(project.getCreatedAt());

        return response;
    }


    @GetMapping("/workspace/{workspaceId}")
    public List<ProjectResponse> getProjectsByWorkspace(
            @PathVariable Long workspaceId,
            Authentication authentication) {

        List<Project> projects = projectService
                .getProjectsByWorkspace(workspaceId, authentication);

        return projects.stream()
                .map(project -> {
                    ProjectResponse response = new ProjectResponse();

                    response.setId(project.getId());
                    response.setName(project.getName());
                    response.setDescription(project.getDescription());
                    response.setWorkspaceId(project.getWorkspace().getId());
                    response.setCreatedBy(project.getCreatedBy().getEmail());
                    response.setCreatedAt(project.getCreatedAt());

                    return response;
                })
                .toList();
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long projectId,
            Authentication authentication) {

        Project project = projectService.getProjectById(
                projectId,
                authentication
        );

        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setCreatedBy(project.getCreatedBy().getEmail());
        response.setWorkspaceId(project.getWorkspace().getId());
        response.setCreatedAt(project.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication) {

        ProjectResponse response = projectService.updateProject(
                projectId,
                request,
                authentication
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<ProjectMemberResponse> addProjectMember(
            @PathVariable Long projectId,
            @Valid @RequestBody AddProjectMemberRequest request,
            Authentication authentication) {

        ProjectMember savedMember = projectService.addProjectMember(
                projectId,
                request,
                authentication
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new ProjectMemberResponse(
                savedMember.getId(),
                savedMember.getProject().getId(),
                savedMember.getUser().getId(),
                savedMember.getUser().getName(),
                savedMember.getUser().getEmail()
        ));
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberResponse>> getProjectMembers(
            @PathVariable Long projectId,
            Authentication authentication) {

        List<ProjectMemberResponse> members =
                projectService.getProjectMembers(projectId, authentication);

        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Void> removeProjectMember(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            Authentication authentication) {

        projectService.removeProjectMember(
                projectId,
                userId,
                authentication
        );

        return ResponseEntity.noContent().build();
    }

}
