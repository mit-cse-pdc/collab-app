package edu.manipal.cse.masterdataservice.repositories;

import edu.manipal.cse.masterdataservice.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    List<Enrollment> findByStudentId(UUID studentId);
    List<Enrollment> findByCourseCourseId(UUID courseId);
    boolean existsByStudentIdAndCourseCourseId(UUID studentId, UUID courseId);
    Optional<Enrollment> findByStudentIdAndCourseCourseId(UUID studentId, UUID courseId);
}