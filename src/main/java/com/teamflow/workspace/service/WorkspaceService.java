
package com.teamflow.workspace.service;

import com.teamflow.common.exception.InvalidCredentialsException;
import com.teamflow.user.entity.User;
import com.teamflow.user.repository.UserRepository;
import com.teamflow.workspace.dto.*;
import com.teamflow.workspace.entity.Workspace;
import com.teamflow.workspace.entity.WorkspaceMembership;
import com.teamflow.workspace.entity.WorkspaceRole;
import com.teamflow.workspace.repository.WorkspaceMembershipRepository;
import com.teamflow.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.teamflow.workspace.dto.UpdateWorkspaceMemberRoleRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMembershipRepository membershipRepository;
    private final UserRepository userRepository;

    @Transactional
    public WorkspaceResponse createWorkspace(
            CreateWorkspaceRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Authenticated user was not found"
                        )
                );

        Workspace workspace = new Workspace();
        workspace.setName(request.getName().trim());
        workspace.setDescription(
                request.getDescription() == null
                        || request.getDescription().isBlank()
                        ? null
                        : request.getDescription().trim()
        );
        workspace.setOwner(owner);

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        WorkspaceMembership membership = new WorkspaceMembership();
        membership.setWorkspace(savedWorkspace);
        membership.setUser(owner);
        membership.setRole(WorkspaceRole.OWNER);

        membershipRepository.save(membership);

        return new WorkspaceResponse(
                savedWorkspace.getId(),
                savedWorkspace.getName(),
                savedWorkspace.getDescription(),
                owner.getId(),
                savedWorkspace.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getMyWorkspaces(
            Authentication authentication) {

        String email = authentication.getName();

        return membershipRepository.findByUserEmail(email)
                .stream()
                .map(membership -> {
                    Workspace workspace = membership.getWorkspace();

                    return new WorkspaceResponse(
                            workspace.getId(),
                            workspace.getName(),
                            workspace.getDescription(),
                            workspace.getOwner().getId(),
                            workspace.getCreatedAt()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkspaceResponse getWorkspaceById(
            Long workspaceId,
            Authentication authentication) {

        String email = authentication.getName();

        boolean isMember = membershipRepository
                .existsByWorkspaceIdAndUserEmail(workspaceId, email);

        if (!isMember) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Workspace not found"
            );
        }

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Workspace not found"
                        )
                );

        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getDescription(),
                workspace.getOwner().getId(),
                workspace.getCreatedAt()
        );
    }


    @Transactional
    public WorkspaceResponse updateWorkspace(
            Long workspaceId,
            UpdateWorkspaceRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        WorkspaceMembership membership = membershipRepository
                .findByWorkspaceIdAndUserEmail(workspaceId, email)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Workspace not found"
                        )
                );

        if (membership.getRole() != WorkspaceRole.OWNER) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Only the workspace owner can update workspace details"
            );
        }

        Workspace workspace = membership.getWorkspace();

        workspace.setName(request.getName().trim());

        workspace.setDescription(
                request.getDescription() == null
                        || request.getDescription().isBlank()
                        ? null
                        : request.getDescription().trim()
        );

        Workspace updatedWorkspace = workspaceRepository.save(workspace);

        return new WorkspaceResponse(
                updatedWorkspace.getId(),
                updatedWorkspace.getName(),
                updatedWorkspace.getDescription(),
                updatedWorkspace.getOwner().getId(),
                updatedWorkspace.getCreatedAt()
        );
    }


    @Transactional
    public WorkspaceMemberResponse addWorkspaceMember(
            Long workspaceId,
            AddWorkspaceMemberRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        WorkspaceMembership ownerMembership = membershipRepository
                .findByWorkspaceIdAndUserEmail(workspaceId, email)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Workspace not found"
                        )
                );

        if (ownerMembership.getRole() != WorkspaceRole.OWNER) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Only the workspace owner can add members"
            );
        }

        if (request.getRole() == WorkspaceRole.OWNER) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "The OWNER role cannot be assigned through this API"
            );
        }

        String memberEmail = request.getEmail().trim();

        User memberUser = userRepository.findByEmail(memberEmail)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        if (membershipRepository.existsByWorkspaceIdAndUserEmail(
                workspaceId, memberEmail)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT,
                    "User is already a member of this workspace"
            );
        }

        WorkspaceMembership membership = new WorkspaceMembership();
        membership.setWorkspace(ownerMembership.getWorkspace());
        membership.setUser(memberUser);
        membership.setRole(request.getRole());

        membershipRepository.save(membership);

        return new WorkspaceMemberResponse(
                memberUser.getId(),
                memberUser.getName(),
                memberUser.getEmail(),
                membership.getRole()
        );
    }


    @Transactional(readOnly = true)
    public List<WorkspaceMemberResponse> getWorkspaceMembers(
            Long workspaceId,
            Authentication authentication) {

        String email = authentication.getName();


        if (!membershipRepository.existsByWorkspaceIdAndUserEmail(
                workspaceId, email)) {

            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Workspace not found"
            );
        }

        List<WorkspaceMembership> memberships =
                membershipRepository.findByWorkspaceId(workspaceId);

        return memberships.stream()
                .map(membership -> new WorkspaceMemberResponse(
                        membership.getUser().getId(),
                        membership.getUser().getName(),
                        membership.getUser().getEmail(),
                        membership.getRole()
                ))
                .toList();
    }


    @Transactional
    public void removeWorkspaceMember(
            Long workspaceId,
            Long userId,
            Authentication authentication) {

        String email = authentication.getName();

        WorkspaceMembership ownerMembership = membershipRepository
                .findByWorkspaceIdAndUserEmail(workspaceId, email)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Workspace not found"
                        )
                );

        if (ownerMembership.getRole() != WorkspaceRole.OWNER) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Only the workspace owner can remove members"
            );
        }

        WorkspaceMembership targetMembership = membershipRepository
                .findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Workspace member not found"
                        )
                );

        if (targetMembership.getRole() == WorkspaceRole.OWNER) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "The workspace owner cannot be removed"
            );
        }

        membershipRepository.delete(targetMembership);
    }


    @Transactional
    public WorkspaceMemberResponse updateWorkspaceMemberRole(
            Long workspaceId,
            Long userId,
            UpdateWorkspaceMemberRoleRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        WorkspaceMembership ownerMembership = membershipRepository
                .findByWorkspaceIdAndUserEmail(workspaceId, email)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Workspace not found"
                        )
                );

        if (ownerMembership.getRole() != WorkspaceRole.OWNER) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Only the workspace owner can change member roles"
            );
        }

        if (request.getRole() == WorkspaceRole.OWNER) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "The OWNER role cannot be assigned through this API"
            );
        }

        WorkspaceMembership targetMembership = membershipRepository
                .findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "Workspace member not found"
                        )
                );

        targetMembership.setRole(request.getRole());

        membershipRepository.save(targetMembership);

        User member = targetMembership.getUser();

        return new WorkspaceMemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                targetMembership.getRole()
        );
    }
}