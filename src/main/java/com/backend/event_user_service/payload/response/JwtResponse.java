package com.backend.event_user_service.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private int id;
    private String username;
    private String email;
    private Boolean profileSet;
    private List<String> roles;

    public JwtResponse(String token, int id, String username, String email, Boolean profileSet, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileSet = profileSet;
        this.roles = roles;

    }
}