package edu.manipal.cse.userservice.repositories;

import edu.manipal.cse.userservice.entities.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, UUID> {

    Optional<Faculty> findByEmail(String email);

    boolean existsByEmail(String email);
}