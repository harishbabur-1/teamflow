
package com.teamflow.workspace.dto;

import com.teamflow.workspace.entity.WorkspaceRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWorkspaceMemberRoleRequest {

    @NotNull(message = "Workspace role is required")
    private WorkspaceRole role;
}