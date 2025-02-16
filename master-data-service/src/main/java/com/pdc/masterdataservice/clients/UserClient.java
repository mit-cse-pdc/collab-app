package com.pdc.masterdataservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", path = "/api/v1")
public interface UserClient {
    @GetMapping("/faculty/exists/{id}")
    ResponseEntity<Boolean> facultyExistsById(@PathVariable UUID id);

    @GetMapping("/students/exists/{id}")
    ResponseEntity<Boolean> studentExistsById(@PathVariable UUID id);
}
