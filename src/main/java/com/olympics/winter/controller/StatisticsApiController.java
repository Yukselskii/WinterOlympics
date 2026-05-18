package com.olympics.winter.controller;

import com.olympics.winter.dto.MedalTableEntry;
import com.olympics.winter.entity.Athlete;
import com.olympics.winter.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class StatisticsApiController {

    private final StatisticsService statisticsService;

    public StatisticsApiController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/medals")
    public List<MedalTableEntry> getMedalTable() {
        return statisticsService.getMedalTable();
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageAthleteAge", statisticsService.getAverageAthleteAge());

        Optional<Athlete> youngest = statisticsService.getYoungestMedalist();
        Optional<Athlete> oldest = statisticsService.getOldestMedalist();

        youngest.ifPresent(a -> stats.put("youngestMedalist", Map.of(
                "id", a.getId(), "name", a.getName(), "birthDate", a.getBirthDate().toString())));
        oldest.ifPresent(a -> stats.put("oldestMedalist", Map.of(
                "id", a.getId(), "name", a.getName(), "birthDate", a.getBirthDate().toString())));

        return stats;
    }
}
