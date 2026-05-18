package com.olympics.winter;

import com.olympics.winter.dto.MedalTableEntry;
import com.olympics.winter.entity.Athlete;
import com.olympics.winter.entity.Medal;
import com.olympics.winter.repository.AthleteRepository;
import com.olympics.winter.repository.MedalRepository;
import com.olympics.winter.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsServiceTest {

    private StatisticsService statisticsService;
    private MedalRepository medalRepository;
    private AthleteRepository athleteRepository;

    @BeforeEach
    void setUp() {
        medalRepository = Mockito.mock(MedalRepository.class);
        athleteRepository = Mockito.mock(AthleteRepository.class);
        statisticsService = new StatisticsService(medalRepository, athleteRepository);
    }

    @Test
    void averageAge_calculatedCorrectly() {
        Athlete a1 = new Athlete("Alice", "USA", Athlete.Gender.FEMALE, LocalDate.of(2000, 1, 1));
        Athlete a2 = new Athlete("Bob", "NOR", Athlete.Gender.MALE, LocalDate.of(1990, 1, 1));
        when(athleteRepository.findAll()).thenReturn(List.of(a1, a2));

        double avg = statisticsService.getAverageAthleteAge();

        // Both ages should be roughly 26 and 36 in 2026 => avg ~31
        assertThat(avg).isBetween(25.0, 40.0);
    }

    @Test
    void youngestMedalist_returnsMostRecentBirthDate() {
        Athlete young = new Athlete("Young", "ITA", Athlete.Gender.FEMALE, LocalDate.of(2002, 5, 1));
        Athlete old = new Athlete("Old", "GER", Athlete.Gender.FEMALE, LocalDate.of(1985, 3, 1));

        Medal m1 = new Medal();
        m1.setAthlete(young);
        Medal m2 = new Medal();
        m2.setAthlete(old);

        when(medalRepository.findAll()).thenReturn(List.of(m1, m2));

        Optional<Athlete> result = statisticsService.getYoungestMedalist();

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Young");
    }
}
