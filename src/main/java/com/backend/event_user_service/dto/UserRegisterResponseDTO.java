package com.backend.event_user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterResponseDTO {
    private String username;
    private String email;
    private Boolean profileSet;
    private Set<String> roles;
}
