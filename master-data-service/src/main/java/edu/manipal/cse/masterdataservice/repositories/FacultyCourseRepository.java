package edu.manipal.cse.masterdataservice.repositories;

import edu.manipal.cse.masterdataservice.entities.FacultyCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacultyCourseRepository extends JpaRepository<FacultyCourse, UUID> {
    List<FacultyCourse> findByFacultyId(UUID facultyId);
    List<FacultyCourse> findByCourseCourseId(UUID courseId);
    boolean existsByFacultyIdAndCourseCourseId(UUID facultyId, UUID courseId);
    Optional<FacultyCourse> findByFacultyIdAndCourseCourseId(UUID facultyId, UUID courseId);
    void deleteByFacultyIdAndCourseCourseId(UUID facultyId, UUID courseId);
}