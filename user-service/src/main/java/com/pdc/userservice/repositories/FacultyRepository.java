package com.pdc.userservice.repositories;

import com.pdc.userservice.entities.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, UUID> {

    Optional<Faculty> findByEmail(String email);

    boolean existsByEmail(String email);
}