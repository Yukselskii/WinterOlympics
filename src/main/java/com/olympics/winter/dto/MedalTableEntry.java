package com.olympics.winter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedalTableEntry {
    private String country;
    private long gold;
    private long silver;
    private long bronze;
    private long total;
}
