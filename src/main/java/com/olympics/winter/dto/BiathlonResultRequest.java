package com.olympics.winter.dto;

public class BiathlonResultRequest {
    private Long athleteId;
    private Double skiingTime;
    private int missedShots;
    private boolean dnf;

    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }
    public Double getSkiingTime() { return skiingTime; }
    public void setSkiingTime(Double skiingTime) { this.skiingTime = skiingTime; }
    public int getMissedShots() { return missedShots; }
    public void setMissedShots(int missedShots) { this.missedShots = missedShots; }
    public boolean isDnf() { return dnf; }
    public void setDnf(boolean dnf) { this.dnf = dnf; }
}
