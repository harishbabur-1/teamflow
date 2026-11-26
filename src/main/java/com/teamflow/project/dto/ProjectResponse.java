
package com.teamflow.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Long workspaceId;
    private String createdBy;
    private LocalDateTime createdAt;
}
