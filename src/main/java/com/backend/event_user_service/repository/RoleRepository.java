package com.backend.event_user_service.repository;

import com.backend.event_user_service.model.ERole;
import com.backend.event_user_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}

