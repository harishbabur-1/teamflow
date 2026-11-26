
package com.teamflow.workspace.dto;

import com.teamflow.workspace.entity.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkspaceMemberResponse {

    private Long userId;
    private String name;
    private String email;
    private WorkspaceRole role;
}