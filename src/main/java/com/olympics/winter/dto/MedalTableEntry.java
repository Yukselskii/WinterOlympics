package com.olympics.winter.dto;

public class MedalTableEntry {
    private String country;
    private long gold;
    private long silver;
    private long bronze;
    private long total;

    public MedalTableEntry(String country, long gold, long silver, long bronze, long total) {
        this.country = country;
        this.gold = gold;
        this.silver = silver;
        this.bronze = bronze;
        this.total = total;
    }

    public String getCountry() { return country; }
    public long getGold() { return gold; }
    public long getSilver() { return silver; }
    public long getBronze() { return bronze; }
    public long getTotal() { return total; }
}
