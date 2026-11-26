package com.teamflow.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectMemberResponse {

    private Long id;
    private Long projectId;
    private Long userId;
    private String userName;
    private String userEmail;
}