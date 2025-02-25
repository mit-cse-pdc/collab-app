package edu.manipal.cse.userservice.clients;

import edu.manipal.cse.userservice.dto.SchoolDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "master-data-service", path = "/api/v1/schools")
public interface SchoolClient {

    @GetMapping("/{schoolId}")
    ResponseEntity<SchoolDto> getSchoolById(@PathVariable UUID schoolId);
}
