
package com.teamflow.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WorkspaceResponse {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private LocalDateTime createdAt;
}