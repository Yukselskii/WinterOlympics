package com.olympics.winter.repository;

import com.olympics.winter.entity.SkiSlalomResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SkiSlalomResultRepository extends JpaRepository<SkiSlalomResult, Long> {
    List<SkiSlalomResult> findByCompetitionId(Long competitionId);
    Optional<SkiSlalomResult> findByCompetitionIdAndAthleteId(Long competitionId, Long athleteId);

    @Query("SELECT r FROM SkiSlalomResult r WHERE r.competition.id = :compId AND r.firstRunDnf = false ORDER BY r.firstRunTime ASC")
    List<SkiSlalomResult> findFinishersOrderByFirstRunTime(@Param("compId") Long competitionId);

    @Query("SELECT r FROM SkiSlalomResult r WHERE r.competition.id = :compId AND r.finalTime IS NOT NULL ORDER BY r.finalTime ASC")
    List<SkiSlalomResult> findFinalRankings(@Param("compId") Long competitionId);
}
