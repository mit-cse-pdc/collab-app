package com.pdc.masterdataservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "faculty_courses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FacultyCourse extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "faculty_course_id")
    private UUID facultyCourseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "faculty_id", nullable = false)
    private UUID facultyId;
}