package com.olympics.winter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "medals")
public class Medal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedalType type;

    public enum MedalType { GOLD, SILVER, BRONZE }

    public Medal() {}

    public Medal(Competition competition, Athlete athlete, MedalType type) {
        this.competition = competition;
        this.athlete = athlete;
        this.type = type;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public Athlete getAthlete() { return athlete; }
    public void setAthlete(Athlete athlete) { this.athlete = athlete; }
    public MedalType getType() { return type; }
    public void setType(MedalType type) { this.type = type; }
}
