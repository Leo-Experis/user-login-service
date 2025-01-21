package com.backend.hotel;

import com.backend.hotel.model.ERole;
import com.backend.hotel.model.Role;
import com.backend.hotel.model.User;
import com.backend.hotel.repository.RoleRepository;
import com.backend.hotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class HotelApplication {

    @Autowired
    private RoleRepository roles;

    @Autowired
    private UserRepository users;

    @Autowired
    PasswordEncoder encoder;

    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
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
        if (users.findByUsername("admin").isEmpty()) {
            String username = "admin";
            String password = encoder.encode("admin");

            Set<Role> rolesToAdd = new HashSet<>();
            Role adminRole = roles.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            rolesToAdd.add(adminRole);
            User newAdmin = new User(username, password);
            newAdmin.setRoles(rolesToAdd);
            users.save(newAdmin);
        }

    }

}
