package com.olympics.winter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "biathlon_results")
public class BiathlonResult {

    public static final double PENALTY_PER_MISS_SECONDS = 60.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    private Double skiingTime;
    private int missedShots = 0;
    private boolean dnf = false;
    private Double penaltyTime;
    private Double finalTime;

    @Column(nullable = false)
    private int finalRank = 0;

    public void calculateFinalTime() {
        if (dnf || skiingTime == null) {
            this.finalTime = null;
            this.penaltyTime = null;
        } else {
            this.penaltyTime = missedShots * PENALTY_PER_MISS_SECONDS;
            this.finalTime = skiingTime + penaltyTime;
        }
    }

    public boolean isFinished() { return finalTime != null; }
}
