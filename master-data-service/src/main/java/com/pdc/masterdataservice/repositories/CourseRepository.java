package com.pdc.masterdataservice.repositories;

import com.pdc.masterdataservice.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    boolean existsByCourseCodeIgnoreCase(String courseCode);
    List<Course> findBySpecializationSpecializationId(UUID specializationId);
    Optional<Course> findByCourseCodeIgnoreCase(String courseCode);
}