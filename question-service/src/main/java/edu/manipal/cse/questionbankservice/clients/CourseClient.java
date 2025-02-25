package edu.manipal.cse.questionbankservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "master-data-service", path = "/api/v1")
public interface CourseClient {

    @GetMapping("/courses/exists/{id}")
    Boolean courseExistsById(@PathVariable  UUID id);
}
