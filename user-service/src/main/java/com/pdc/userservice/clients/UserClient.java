package com.pdc.userservice.clients;

import com.pdc.userservice.dto.response.FacultyResponse;
import com.pdc.userservice.dto.response.StudentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", path = "/api/v1")
public interface UserClient {
    @GetMapping("/faculty/{facultyId}")
    ResponseEntity<FacultyResponse> getFacultyById(@PathVariable UUID facultyId);

    @GetMapping("/students/{studentId}")
    ResponseEntity<StudentResponse> getStudentById(@PathVariable UUID studentId);
}
