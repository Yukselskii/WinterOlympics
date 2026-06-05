package com.olympics.winter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "athletes")
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @NotNull
    @Column(nullable = false)
    private LocalDate birthDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "athlete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompetitionRegistration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "athlete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medal> medals = new ArrayList<>();

    public enum Gender { MALE, FEMALE }

    public Athlete() {}

    public Athlete(String name, String country, Gender gender, LocalDate birthDate) {
        this.name = name;
        this.country = country;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
