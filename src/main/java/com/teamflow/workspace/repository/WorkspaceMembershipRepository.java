
package com.teamflow.workspace.repository;

import com.teamflow.workspace.entity.WorkspaceMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMembershipRepository
        extends JpaRepository<WorkspaceMembership, Long> {

    List<WorkspaceMembership> findByUserId(Long userId);

    List<WorkspaceMembership> findByWorkspaceId(Long workspaceId);

    Optional<WorkspaceMembership> findByWorkspaceIdAndUserId(
            Long workspaceId,
            Long userId
    );


    List<WorkspaceMembership> findByUserEmail(String email);

    boolean existsByWorkspaceIdAndUserEmail(Long workspaceId, String email);

    Optional<WorkspaceMembership> findByWorkspaceIdAndUserEmail(
            Long workspaceId,
            String email
    );


    void deleteByWorkspaceIdAndUserId(Long workspaceId, Long userId);
}