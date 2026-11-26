
package com.teamflow.workspace.dto;

import com.teamflow.workspace.entity.WorkspaceRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddWorkspaceMemberRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotNull(message = "Workspace role is required")
    private WorkspaceRole role;
}