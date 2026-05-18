package com.olympics.winter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ski_slalom_results")
public class SkiSlalomResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    private Double firstRunTime;
    private Double secondRunTime;
    private boolean firstRunDnf = false;
    private boolean secondRunDnf = false;
    private boolean advancedToSecondRun = false;
    private Double finalTime;

    @Column(nullable = false)
    private int finalRank = 0;

    public void calculateFinalTime() {
        if (firstRunDnf || secondRunDnf) {
            this.finalTime = null;
        } else if (firstRunTime != null && secondRunTime != null) {
            this.finalTime = firstRunTime + secondRunTime;
        }
    }

    public boolean isFinished() { return finalTime != null; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public Athlete getAthlete() { return athlete; }
    public void setAthlete(Athlete athlete) { this.athlete = athlete; }
    public Double getFirstRunTime() { return firstRunTime; }
    public void setFirstRunTime(Double firstRunTime) { this.firstRunTime = firstRunTime; }
    public Double getSecondRunTime() { return secondRunTime; }
    public void setSecondRunTime(Double secondRunTime) { this.secondRunTime = secondRunTime; }
    public boolean isFirstRunDnf() { return firstRunDnf; }
    public void setFirstRunDnf(boolean firstRunDnf) { this.firstRunDnf = firstRunDnf; }
    public boolean isSecondRunDnf() { return secondRunDnf; }
    public void setSecondRunDnf(boolean secondRunDnf) { this.secondRunDnf = secondRunDnf; }
    public boolean isAdvancedToSecondRun() { return advancedToSecondRun; }
    public void setAdvancedToSecondRun(boolean advancedToSecondRun) { this.advancedToSecondRun = advancedToSecondRun; }
    public Double getFinalTime() { return finalTime; }
    public void setFinalTime(Double finalTime) { this.finalTime = finalTime; }
    public int getFinalRank() { return finalRank; }
    public void setFinalRank(int finalRank) { this.finalRank = finalRank; }
}
