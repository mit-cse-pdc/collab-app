package edu.manipal.cse.masterdataservice.repositories;

import edu.manipal.cse.masterdataservice.entities.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SchoolRepository extends JpaRepository<School, UUID> {

    Boolean existsByNameIgnoreCase(String name);
}