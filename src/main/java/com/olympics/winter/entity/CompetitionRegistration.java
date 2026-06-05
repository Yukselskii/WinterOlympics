package com.olympics.winter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
