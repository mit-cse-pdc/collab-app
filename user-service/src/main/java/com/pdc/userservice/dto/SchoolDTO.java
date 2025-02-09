package com.pdc.userservice.dto;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SchoolDTO {
    private String name;

    private UUID schoolId;
}
