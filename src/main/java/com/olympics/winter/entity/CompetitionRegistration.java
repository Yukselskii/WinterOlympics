package com.olympics.winter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "competition_registrations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"athlete_id", "competition_id"}))
public class CompetitionRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    public CompetitionRegistration() {}

    public CompetitionRegistration(Athlete athlete, Competition competition) {
        this.athlete = athlete;
        this.competition = competition;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Athlete getAthlete() { return athlete; }
    public void setAthlete(Athlete athlete) { this.athlete = athlete; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
}
