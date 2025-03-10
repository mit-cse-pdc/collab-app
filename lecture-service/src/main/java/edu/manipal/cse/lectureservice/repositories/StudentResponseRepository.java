package edu.manipal.cse.lectureservice.repositories;

import edu.manipal.cse.lectureservice.models.StudentResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentResponseRepository extends JpaRepository<StudentResponse, UUID> {
}