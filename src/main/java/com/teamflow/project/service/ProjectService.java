
package com.teamflow.project.service;

import com.teamflow.project.dto.AddProjectMemberRequest;
import com.teamflow.project.dto.CreateProjectRequest;

import com.teamflow.project.dto.ProjectMemberResponse;
import com.teamflow.project.dto.ProjectResponse;
import com.teamflow.project.entity.Project;
import com.teamflow.project.entity.ProjectMember;
import com.teamflow.project.repository.ProjectMemberRepository;
import com.teamflow.project.repository.ProjectRepository;

import com.teamflow.user.entity.User;
import com.teamflow.user.repository.UserRepository;
import com.teamflow.workspace.entity.Workspace;
import com.teamflow.workspace.entity.WorkspaceMembership;
import com.teamflow.workspace.entity.WorkspaceRole;
import com.teamflow.workspace.repository.WorkspaceRepository;
import com.teamflow.workspace.repository.WorkspaceMembershipRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMembershipRepository membershipRepository;
    private final UserRepository userRepository;

    @Transactional
    public Project createProject(
            CreateProjectRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"));

        Workspace workspace = workspaceRepository.findById(
                        request.getWorkspaceId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Workspace not found"));

        boolean isMember =
                membershipRepository.existsByWorkspaceIdAndUserEmail(
                        workspace.getId(), email);

        if (!isMember) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Workspace not found");
        }

        Project project = new Project();

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setWorkspace(workspace);
        project.setCreatedBy(user);

        projectRepository.save(project);
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProject(project);
        projectMember.setUser(user);

        projectMemberRepository.save(projectMember);
        return project;
    }


    public List<Project> getProjectsByWorkspace(Long workspaceId, Authentication authentication) {

        String email = authentication.getName();

        boolean isMember = membershipRepository
                .existsByWorkspaceIdAndUserEmail(workspaceId, email);

        if (!isMember) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Workspace not found"
            );
        }

        return projectRepository.findByWorkspaceId(workspaceId);
    }

    public Project getProjectById(Long projectId, Authentication authentication) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project not found"
                        )
                );

        String email = authentication.getName();

        boolean isMember = membershipRepository
                .existsByWorkspaceIdAndUserEmail(
                        project.getWorkspace().getId(),
                        email
                );

        if (!isMember) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Project not found"
            );
        }

        return project;
    }

    @Transactional
    public ProjectResponse updateProject(
            Long projectId,
            CreateProjectRequest request,
            Authentication authentication) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project not found"
                        )
                );

        String email = authentication.getName();

        WorkspaceMembership membership = membershipRepository
                .findByWorkspaceIdAndUserEmail(
                        project.getWorkspace().getId(),
                        email
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project not found"
                        )
                );

        boolean isProjectCreator =
                project.getCreatedBy().getEmail().equals(email);

        WorkspaceRole role = membership.getRole();

        boolean isWorkspaceAdmin =
                role == WorkspaceRole.OWNER
                        || role == WorkspaceRole.MANAGER;

        if (!isProjectCreator && !isWorkspaceAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to update this project"
            );
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        projectRepository.save(project);

        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setCreatedBy(project.getCreatedBy().getEmail());
        response.setWorkspaceId(project.getWorkspace().getId());
        response.setCreatedAt(project.getCreatedAt());

        return response;
    }

    @Transactional
    public ProjectMember addProjectMember(
            Long projectId,
            AddProjectMemberRequest request,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        Long workspaceId = project.getWorkspace().getId();

        WorkspaceMembership actorMembership =
                membershipRepository
                        .findByWorkspaceIdAndUserEmail(
                                workspaceId,
                                currentUserEmail
                        )
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "You are not a member of this workspace"
                        ));

        boolean isProjectCreator =
                project.getCreatedBy().getEmail().equals(currentUserEmail);

        WorkspaceRole role = actorMembership.getRole();

        boolean isWorkspaceAdmin =
                role == WorkspaceRole.OWNER
                        || role == WorkspaceRole.MANAGER;

        if (!isProjectCreator && !isWorkspaceAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to add project members"
            );
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        boolean isWorkspaceMember =
                membershipRepository
                        .existsByWorkspaceIdAndUserEmail(
                                workspaceId,
                                user.getEmail()
                        );

        if (!isWorkspaceMember) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User is not a member of this workspace"
            );
        }

        boolean alreadyExists =
                projectMemberRepository
                        .existsByProjectIdAndUserId(
                                projectId,
                                user.getId()
                        );

        if (alreadyExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User is already a project member"
            );
        }

        ProjectMember projectMember = new ProjectMember();
        projectMember.setProject(project);
        projectMember.setUser(user);

        return projectMemberRepository.save(projectMember);
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getProjectMembers(
            Long projectId,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        Long workspaceId = project.getWorkspace().getId();

        boolean isWorkspaceMember =
                membershipRepository.existsByWorkspaceIdAndUserEmail(
                        workspaceId,
                        currentUserEmail
                );

        if (!isWorkspaceMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not a member of this workspace"
            );
        }

        List<ProjectMember> members =
                projectMemberRepository.findByProjectId(projectId);

        return members.stream()
                .map(member -> new ProjectMemberResponse(
                        member.getId(),
                        member.getProject().getId(),
                        member.getUser().getId(),
                        member.getUser().getName(),
                        member.getUser().getEmail()
                ))
                .toList();
    }

    @Transactional
    public void removeProjectMember(
            Long projectId,
            Long userId,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        Long workspaceId = project.getWorkspace().getId();

        WorkspaceMembership membership =
                membershipRepository
                        .findByWorkspaceIdAndUserEmail(
                                workspaceId,
                                currentUserEmail
                        )
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "You are not a member of this workspace"
                        ));

        boolean isProjectCreator =
                project.getCreatedBy().getEmail().equals(currentUserEmail);

        WorkspaceRole role = membership.getRole();

        boolean isWorkspaceAdmin =
                role == WorkspaceRole.OWNER
                        || role == WorkspaceRole.MANAGER;

        if (!isProjectCreator && !isWorkspaceAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to remove project members"
            );
        }

        ProjectMember projectMember =
                projectMemberRepository.findByProjectIdAndUserId(
                        projectId,
                        userId
                ).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project member not found"
                ));

        projectMemberRepository.delete(projectMember);
    }

}
