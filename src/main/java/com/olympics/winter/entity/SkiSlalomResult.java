package com.olympics.winter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
