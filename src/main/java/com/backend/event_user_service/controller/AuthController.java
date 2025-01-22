package com.backend.event_user_service.controller;

import com.backend.event_user_service.dto.UserRegisterResponseDTO;
import com.backend.event_user_service.model.ERole;
import com.backend.event_user_service.model.Role;
import com.backend.event_user_service.model.User;
import com.backend.event_user_service.payload.LoginRequest;
import com.backend.event_user_service.payload.SignupRequest;
import com.backend.event_user_service.payload.response.JwtResponse;
import com.backend.event_user_service.repository.RoleRepository;
import com.backend.event_user_service.repository.UserRepository;
import com.backend.event_user_service.responses.ErrorResponse;
import com.backend.event_user_service.responses.Response;
import com.backend.event_user_service.responses.UserSignUpSuccessResponse;
import com.backend.event_user_service.security.jwt.JwtUtils;
import com.backend.event_user_service.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("auth")
@RestController
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername().isEmpty() || loginRequest.getPassword().isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.set("Bad input");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.set("incorrect username or password");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity
                .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<Response<?>> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        String givenUsername = signupRequest.getUsername();
        String givenPassword = signupRequest.getPassword();
        ErrorResponse errorResponse = new ErrorResponse();

        // Check if username and password are given and not empty
        if (givenPassword == null || givenUsername == null
                || givenUsername.isEmpty() || givenPassword.isEmpty()) {
            errorResponse.set("Bad input");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Check if given username already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            errorResponse.set("Error: Username is already taken");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Create a new user add salt here if using one
        User user = new User(signupRequest.getUsername(), encoder.encode(signupRequest.getPassword()));
        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        // If no roles are given, create a user with the role ROLE_USER
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        } else {
            for (String role : strRoles) {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(adminRole);
                        break;
                   case "user":
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                        break;
                   default:
                        errorResponse.set("Error: Given role '" + role + "' is not a valid role. Sign up failed.");
                        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }
            }
        }
        user.setRoles(roles);
        userRepository.save(user);

        // Create a response object
        UserRegisterResponseDTO userRegisterResponseDTO = new UserRegisterResponseDTO();
        userRegisterResponseDTO.setUsername(user.getUsername());
        userRegisterResponseDTO.setRoles(strRoles);

        UserSignUpSuccessResponse userSignUpSuccessResponse = new UserSignUpSuccessResponse();
        userSignUpSuccessResponse.set(userRegisterResponseDTO);
        return new ResponseEntity<>(userSignUpSuccessResponse, HttpStatus.CREATED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException exception) {
        String errorMsg = exception.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(exception.getMessage());
        ErrorResponse error = new ErrorResponse();
        error.set(errorMsg);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
