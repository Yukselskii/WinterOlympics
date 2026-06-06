package com.olympics.winter.dto;

import lombok.Data;

@Data
public class SkiSlalomResultRequest {
    private Long athleteId;
    private Double firstRunTime;
    private boolean firstRunDnf;
    private Double secondRunTime;
    private boolean secondRunDnf;
}
