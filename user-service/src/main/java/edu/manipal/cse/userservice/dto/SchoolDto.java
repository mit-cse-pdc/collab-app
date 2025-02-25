package edu.manipal.cse.userservice.dto;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SchoolDto {
    private String name;

    private UUID schoolId;
}
