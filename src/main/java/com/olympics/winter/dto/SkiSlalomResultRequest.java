package com.olympics.winter.dto;

public class SkiSlalomResultRequest {
    private Long athleteId;
    private Double firstRunTime;
    private boolean firstRunDnf;
    private Double secondRunTime;
    private boolean secondRunDnf;

    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }
    public Double getFirstRunTime() { return firstRunTime; }
    public void setFirstRunTime(Double firstRunTime) { this.firstRunTime = firstRunTime; }
    public boolean isFirstRunDnf() { return firstRunDnf; }
    public void setFirstRunDnf(boolean firstRunDnf) { this.firstRunDnf = firstRunDnf; }
    public Double getSecondRunTime() { return secondRunTime; }
    public void setSecondRunTime(Double secondRunTime) { this.secondRunTime = secondRunTime; }
    public boolean isSecondRunDnf() { return secondRunDnf; }
    public void setSecondRunDnf(boolean secondRunDnf) { this.secondRunDnf = secondRunDnf; }
}
