package com.olympics.winter.repository;

import com.olympics.winter.entity.Athlete;
import com.olympics.winter.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    List<Competition> findByType(Competition.CompetitionType type);
    List<Competition> findByGender(Athlete.Gender gender);
    List<Competition> findByStatus(Competition.CompetitionStatus status);
}
