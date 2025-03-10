package edu.manipal.cse.lectureservice.repositories;

import edu.manipal.cse.lectureservice.models.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LectureRepository extends JpaRepository<Lecture, UUID> {
}