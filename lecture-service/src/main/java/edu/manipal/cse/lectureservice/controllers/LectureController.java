package edu.manipal.cse.lectureservice.controllers;

import edu.manipal.cse.lectureservice.models.Lecture;
import edu.manipal.cse.lectureservice.services.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;

    @QueryMapping
    public List<Lecture> getAllLectures() {
        return lectureService.getAllLectures();
    }

    @SubscriptionMapping
    public  Flux<List<Lecture>> getLectures() { return lectureService.getLectures(); }

}
