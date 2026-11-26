
package com.teamflow.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RegisterResponse {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}