package edu.manipal.cse.lectureservice.clients;

import edu.manipal.cse.lectureservice.dto.ApiResponse;
import edu.manipal.cse.lectureservice.dto.QuestionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@Component
@FeignClient(name = "question-service", path = "/api/v1")
public interface QuestionClient {

    @PostMapping("/questions/validate")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> validateAllQuestions(List<UUID> uuids);
}
