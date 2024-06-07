package ru.taf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "person")
public class Person implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL)
    private MemoryPage memoryPage;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "epitaph")
    private String epitaph;

    @Column(name = "citizenship")
    private String citizenship;

    @Column(name = "burial_place")
    private String burialPlace;

    @Column(name = "spouse")
    private String spouse;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "education")
    private String education;

    @Column(name = "awards")
    private String awards;

    @OneToMany(mappedBy = "parent")
    private List<Children> children;
}
