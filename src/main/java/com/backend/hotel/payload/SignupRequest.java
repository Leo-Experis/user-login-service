package com.backend.hotel.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "Username cannot be blank and must be between 3 and 20 characters")
    @Size(min = 3, max = 20)
    private String username;

    private Set<String> role;

    @NotBlank(message = "Password cannot be blank and must be between 6 and 40 characters")
    @Size(min = 6, max = 40)
    private String password;

}