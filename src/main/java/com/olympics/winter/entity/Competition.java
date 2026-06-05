package com.olympics.winter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "competitions")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompetitionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Athlete.Gender gender;

    @Min(14)
    @Column(nullable = false)
    private int minimumAge;

    @NotNull
    private LocalDate competitionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompetitionStatus status = CompetitionStatus.UPCOMING;

    private Integer topCutoff = 30;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompetitionRegistration> registrations = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkiSlalomResult> skiSlalomResults = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BiathlonResult> biathlonResults = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medal> medals = new ArrayList<>();

    public enum CompetitionType { SKI_SLALOM, BIATHLON }
    public enum CompetitionStatus { UPCOMING, FIRST_RUN_DONE, COMPLETED }

    public Competition() {}

    public Competition(String name, CompetitionType type, Athlete.Gender gender, int minimumAge, LocalDate competitionDate) {
        this.name = name;
        this.type = type;
        this.gender = gender;
        this.minimumAge = minimumAge;
        this.competitionDate = competitionDate;
    }
}
