package com.pdc.userservice.repositories;

import com.pdc.userservice.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByRegistrationNo(String registrationNo);
    boolean existsByEmail(String email);
    boolean existsByRegistrationNo(String registrationNo);
}