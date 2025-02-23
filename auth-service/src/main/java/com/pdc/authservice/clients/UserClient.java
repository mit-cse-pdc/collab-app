package com.pdc.authservice.clients;

import com.pdc.authservice.dto.FacultyDTO;
import com.pdc.authservice.dto.StudentDTO;
import com.pdc.authservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", path = "/api/v1")
public interface UserClient {
    @GetMapping("/faculty/email/{email}")
    ResponseEntity<ApiResponse<FacultyDTO>> getFacultyByEmail(@PathVariable String email);

    @GetMapping("/faculty/{id}")
    ResponseEntity<ApiResponse<FacultyDTO>> getFacultyById(@PathVariable UUID id);

    @GetMapping("/students/email/{email}")
    ResponseEntity<ApiResponse<StudentDTO>> getStudentByEmail(@PathVariable String email);

    @GetMapping("/students/{id}")
    ResponseEntity<ApiResponse<StudentDTO>> getStudentById(@PathVariable UUID id);
}