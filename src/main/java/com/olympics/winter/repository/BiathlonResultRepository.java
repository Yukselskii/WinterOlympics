package com.olympics.winter.repository;

import com.olympics.winter.entity.BiathlonResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BiathlonResultRepository extends JpaRepository<BiathlonResult, Long> {
    List<BiathlonResult> findByCompetitionId(Long competitionId);
    Optional<BiathlonResult> findByCompetitionIdAndAthleteId(Long competitionId, Long athleteId);

    @Query("SELECT r FROM BiathlonResult r WHERE r.competition.id = :compId AND r.finalTime IS NOT NULL ORDER BY r.finalTime ASC")
    List<BiathlonResult> findFinalRankings(@Param("compId") Long competitionId);
}
