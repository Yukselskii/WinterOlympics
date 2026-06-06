package com.olympics.winter.dto;

import lombok.Data;

@Data
public class BiathlonResultRequest {
    private Long athleteId;
    private Double skiingTime;
    private int missedShots;
    private boolean dnf;
}
