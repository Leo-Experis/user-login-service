package com.backend.event_user_service;

import com.backend.event_user_service.model.ERole;
import com.backend.event_user_service.model.Role;
import com.backend.event_user_service.model.User;
import com.backend.event_user_service.repository.RoleRepository;
import com.backend.event_user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class EventUserServiceApplication {

    @Autowired
    private RoleRepository roles;

    @Autowired
    private UserRepository users;

    @Autowired
    PasswordEncoder encoder;

    public static void main(String[] args) {
        SpringApplication.run(EventUserServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doThisAfterStartup() {
        // Insert roles into the database (if they don't exist already)
        for (ERole role : ERole.values()) {
            if (roles.findByName(role).isEmpty()) {
                roles.save(new Role(role));
            }
        }

        // Insert a default admin user into the database (if it doesn't exist already)
        if (!users.existsByUsername("admin")) {
            String username = "admin";
            String password = encoder.encode("admin");
            String email = "admin@admin.com";

            Set<Role> rolesToAdd = new HashSet<>();
            Role adminRole = roles.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            rolesToAdd.add(adminRole);
            User newAdmin = new User(username, password, email, true, 1);
            newAdmin.setRoles(rolesToAdd);
            users.save(newAdmin);
        }

    }

}
