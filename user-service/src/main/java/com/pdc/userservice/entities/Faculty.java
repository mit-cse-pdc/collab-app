package com.pdc.userservice.entities;

import com.pdc.userservice.entities.enums.Position;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "faculty")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Faculty extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "faculty_id", updatable = false, nullable = false)
    private UUID facultyId;

    @Column(name = "school_id", nullable = false)
    private UUID schoolId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false, columnDefinition = "faculty_position")
    private Position position;
}