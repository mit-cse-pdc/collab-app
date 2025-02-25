package edu.manipal.cse.masterdataservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "specializations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Specialization extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "specialization_id")
    private UUID specializationId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;
}