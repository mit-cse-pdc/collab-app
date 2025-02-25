package edu.manipal.cse.masterdataservice.repositories;

import edu.manipal.cse.masterdataservice.entities.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {
    Boolean existsByNameIgnoreCase(String name);
    List<Specialization> findBySchoolSchoolId(UUID schoolId);
}