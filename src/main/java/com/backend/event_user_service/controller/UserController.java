package com.backend.event_user_service.controller;

import com.backend.event_user_service.dto.UpdateProfileSetDTO;
import com.backend.event_user_service.model.User;
import com.backend.event_user_service.repository.UserRepository;
import com.backend.event_user_service.responses.ErrorResponse;
import com.backend.event_user_service.responses.Response;
import com.backend.event_user_service.responses.UserListResponse;
import com.backend.event_user_service.responses.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserRepository repository;

    @GetMapping
    public ResponseEntity<Response<?>> getAll() {
        List<User> users = ResponseEntity.ok(repository.findAll()).getBody();
        UserListResponse userListResponse = new UserListResponse();
        userListResponse.set(users);
        return ResponseEntity.ok(userListResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<Response<?>> getById(@PathVariable Integer id) {
        User user = this.repository.findById(id).orElse(null);
        if(user == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.set("User not Found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        UserResponse userResponse = new UserResponse();
        userResponse.set(user);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping
    public ResponseEntity<Response<?>> create(@RequestBody User user) {
        User newUser = repository.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.set(newUser);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity<Response<?>> update(@PathVariable Integer id, @RequestBody User updatedUser) {
        User oldUser = this.repository.findById(id).orElse(null);
        if(oldUser == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.set("User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        oldUser.setUsername(updatedUser.getUsername());
        oldUser.setPassword(updatedUser.getPassword());
        oldUser.setRoles(updatedUser.getRoles());

        User newUser = repository.save(oldUser);
        UserResponse userResponse = new UserResponse();
        userResponse.set(newUser);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PutMapping("/profile/{username}")
    public ResponseEntity<Response<?>> updateProfileSet(@PathVariable String username, @RequestBody UpdateProfileSetDTO input) {
        User oldUser = repository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username" + username));

        if(oldUser == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.set("User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        oldUser.setProfileSet(input.isProfileSet());
        oldUser.setProfileID(input.getProfileID());

        User newUser = repository.save(oldUser);
        UserResponse userResponse = new UserResponse();
        userResponse.set(newUser);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }
}
