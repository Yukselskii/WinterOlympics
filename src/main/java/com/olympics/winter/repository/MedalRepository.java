package com.olympics.winter.repository;

import com.olympics.winter.entity.Medal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MedalRepository extends JpaRepository<Medal, Long> {
    List<Medal> findByCompetitionId(Long competitionId);
    List<Medal> findByAthleteId(Long athleteId);

    @Query("SELECT m.athlete.country, COUNT(m) FROM Medal m GROUP BY m.athlete.country ORDER BY COUNT(m) DESC")
    List<Object[]> countMedalsByCountry();

    @Query("SELECT m.athlete.country, m.type, COUNT(m) FROM Medal m GROUP BY m.athlete.country, m.type ORDER BY m.athlete.country")
    List<Object[]> countMedalsByCountryAndType();
}
