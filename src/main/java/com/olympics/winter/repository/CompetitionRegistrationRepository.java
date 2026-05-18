package com.olympics.winter.repository;

import com.olympics.winter.entity.CompetitionRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompetitionRegistrationRepository extends JpaRepository<CompetitionRegistration, Long> {
    List<CompetitionRegistration> findByCompetitionId(Long competitionId);
    List<CompetitionRegistration> findByAthleteId(Long athleteId);
    Optional<CompetitionRegistration> findByAthleteIdAndCompetitionId(Long athleteId, Long competitionId);
    boolean existsByAthleteIdAndCompetitionId(Long athleteId, Long competitionId);
}
