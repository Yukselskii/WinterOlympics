package com.olympics.winter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
