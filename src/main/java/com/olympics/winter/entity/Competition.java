package com.olympics.winter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompetitionRegistration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkiSlalomResult> skiSlalomResults = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BiathlonResult> biathlonResults = new ArrayList<>();

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CompetitionType getType() { return type; }
    public void setType(CompetitionType type) { this.type = type; }
    public Athlete.Gender getGender() { return gender; }
    public void setGender(Athlete.Gender gender) { this.gender = gender; }
    public int getMinimumAge() { return minimumAge; }
    public void setMinimumAge(int minimumAge) { this.minimumAge = minimumAge; }
    public LocalDate getCompetitionDate() { return competitionDate; }
    public void setCompetitionDate(LocalDate competitionDate) { this.competitionDate = competitionDate; }
    public CompetitionStatus getStatus() { return status; }
    public void setStatus(CompetitionStatus status) { this.status = status; }
    public Integer getTopCutoff() { return topCutoff; }
    public void setTopCutoff(Integer topCutoff) { this.topCutoff = topCutoff; }
    public List<CompetitionRegistration> getRegistrations() { return registrations; }
    public List<SkiSlalomResult> getSkiSlalomResults() { return skiSlalomResults; }
    public List<BiathlonResult> getBiathlonResults() { return biathlonResults; }
    public List<Medal> getMedals() { return medals; }
}
