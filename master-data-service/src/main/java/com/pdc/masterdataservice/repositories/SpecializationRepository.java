package com.pdc.masterdataservice.repositories;

import com.pdc.masterdataservice.entities.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {
    Boolean existsByNameIgnoreCase(String name);
    List<Specialization> findBySchoolSchoolId(UUID schoolId);
}