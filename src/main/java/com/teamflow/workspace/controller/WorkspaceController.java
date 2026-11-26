
package com.teamflow.workspace.controller;

import com.teamflow.workspace.dto.*;
import com.teamflow.workspace.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request,
            Authentication authentication) {

        WorkspaceResponse response =
                workspaceService.createWorkspace(request, authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getMyWorkspaces(
            Authentication authentication) {

        return ResponseEntity.ok(
                workspaceService.getMyWorkspaces(authentication)
        );
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspaceById(
            @PathVariable Long workspaceId,
            Authentication authentication) {

        return ResponseEntity.ok(
                workspaceService.getWorkspaceById(
                        workspaceId,
                        authentication
                )
        );
    }


    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> updateWorkspace(
            @PathVariable Long workspaceId,
            @Valid @RequestBody UpdateWorkspaceRequest request,
            Authentication authentication) {

        WorkspaceResponse response = workspaceService.updateWorkspace(
                workspaceId,
                request,
                authentication
        );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<WorkspaceMemberResponse> addWorkspaceMember(
            @PathVariable Long workspaceId,
            @Valid @RequestBody AddWorkspaceMemberRequest request,
            Authentication authentication) {

        WorkspaceMemberResponse response =
                workspaceService.addWorkspaceMember(
                        workspaceId,
                        request,
                        authentication
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<List<WorkspaceMemberResponse>> getWorkspaceMembers(
            @PathVariable Long workspaceId,
            Authentication authentication) {

        return ResponseEntity.ok(
                workspaceService.getWorkspaceMembers(
                        workspaceId,
                        authentication
                )
        );
    }


    @DeleteMapping("/{workspaceId}/members/{userId}")
    public ResponseEntity<Void> removeWorkspaceMember(
            @PathVariable Long workspaceId,
            @PathVariable Long userId,
            Authentication authentication) {

        workspaceService.removeWorkspaceMember(
                workspaceId,
                userId,
                authentication
        );

        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{workspaceId}/members/{userId}/role")
    public ResponseEntity<WorkspaceMemberResponse> updateWorkspaceMemberRole(
            @PathVariable Long workspaceId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateWorkspaceMemberRoleRequest request,
            Authentication authentication) {

        WorkspaceMemberResponse response =
                workspaceService.updateWorkspaceMemberRole(
                        workspaceId,
                        userId,
                        request,
                        authentication
                );

        return ResponseEntity.ok(response);
    }

}