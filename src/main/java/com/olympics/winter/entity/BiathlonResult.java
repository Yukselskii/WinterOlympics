package com.olympics.winter.entity;

import jakarta.persistence.*;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public Athlete getAthlete() { return athlete; }
    public void setAthlete(Athlete athlete) { this.athlete = athlete; }
    public Double getSkiingTime() { return skiingTime; }
    public void setSkiingTime(Double skiingTime) { this.skiingTime = skiingTime; }
    public int getMissedShots() { return missedShots; }
    public void setMissedShots(int missedShots) { this.missedShots = missedShots; }
    public boolean isDnf() { return dnf; }
    public void setDnf(boolean dnf) { this.dnf = dnf; }
    public Double getPenaltyTime() { return penaltyTime; }
    public void setPenaltyTime(Double penaltyTime) { this.penaltyTime = penaltyTime; }
    public Double getFinalTime() { return finalTime; }
    public void setFinalTime(Double finalTime) { this.finalTime = finalTime; }
    public int getFinalRank() { return finalRank; }
    public void setFinalRank(int finalRank) { this.finalRank = finalRank; }
}
