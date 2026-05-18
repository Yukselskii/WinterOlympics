package com.olympics.winter.service;

import com.olympics.winter.dto.MedalTableEntry;
import com.olympics.winter.entity.Athlete;
import com.olympics.winter.entity.Medal;
import com.olympics.winter.repository.AthleteRepository;
import com.olympics.winter.repository.MedalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private final MedalRepository medalRepository;
    private final AthleteRepository athleteRepository;

    public StatisticsService(MedalRepository medalRepository, AthleteRepository athleteRepository) {
        this.medalRepository = medalRepository;
        this.athleteRepository = athleteRepository;
    }

    public List<MedalTableEntry> getMedalTable() {
        List<Object[]> raw = medalRepository.countMedalsByCountryAndType();
        Map<String, long[]> countryMedals = new LinkedHashMap<>();

        for (Object[] row : raw) {
            String country = (String) row[0];
            Medal.MedalType type = (Medal.MedalType) row[1];
            long count = (Long) row[2];

            countryMedals.computeIfAbsent(country, k -> new long[3]);
            if (type == Medal.MedalType.GOLD) countryMedals.get(country)[0] += count;
            else if (type == Medal.MedalType.SILVER) countryMedals.get(country)[1] += count;
            else if (type == Medal.MedalType.BRONZE) countryMedals.get(country)[2] += count;
        }

        List<MedalTableEntry> table = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : countryMedals.entrySet()) {
            long[] m = entry.getValue();
            table.add(new MedalTableEntry(entry.getKey(), m[0], m[1], m[2], m[0] + m[1] + m[2]));
        }

        table.sort(Comparator.comparingLong(MedalTableEntry::getGold).reversed());
        return table;
    }

    public double getAverageAthleteAge() {
        List<Athlete> athletes = athleteRepository.findAll();
        if (athletes.isEmpty()) return 0;
        return athletes.stream()
                .mapToInt(a -> Period.between(a.getBirthDate(), LocalDate.now()).getYears())
                .average()
                .orElse(0);
    }

    public Optional<Athlete> getYoungestMedalist() {
        return medalRepository.findAll().stream()
                .map(Medal::getAthlete)
                .max(Comparator.comparing(Athlete::getBirthDate));
    }

    public Optional<Athlete> getOldestMedalist() {
        return medalRepository.findAll().stream()
                .map(Medal::getAthlete)
                .min(Comparator.comparing(Athlete::getBirthDate));
    }
}
