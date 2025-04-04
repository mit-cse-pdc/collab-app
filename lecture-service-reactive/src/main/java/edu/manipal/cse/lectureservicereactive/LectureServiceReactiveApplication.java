package edu.manipal.cse.lectureservicereactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@SpringBootApplication
@EnableDiscoveryClient
@EnableR2dbcAuditing
public class LectureServiceReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(LectureServiceReactiveApplication.class, args);
    }

}
