package com.teamflow.project.controller;

import com.teamflow.project.entity.Project;
import com.teamflow.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/test/projects")
@RequiredArgsConstructor
public class ProjectFetchTestController {

    private final ProjectRepository projectRepository;

    @GetMapping("/{id}")
    public ProjectFetchTestResponse testFetch(@PathVariable Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"));

        boolean workspaceLoaded =
                Hibernate.isInitialized(project.getWorkspace());

        boolean createdByLoaded =
                Hibernate.isInitialized(project.getCreatedBy());

        return new ProjectFetchTestResponse(
                project.getId(),
                project.getName(),
                project.getWorkspace().getId(),
                project.getCreatedBy().getEmail(),
                workspaceLoaded,
                createdByLoaded
        );
    }

    public record ProjectFetchTestResponse(
            Long projectId,
            String projectName,
            Long workspaceId,
            String createdByEmail,
            boolean workspaceLoaded,
            boolean createdByLoaded
    ) {
    }
}