package com.olympics.winter.repository;

import com.olympics.winter.entity.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {
    Optional<Athlete> findByUserId(Long userId);
    List<Athlete> findByCountry(String country);

    @Query("SELECT a FROM Athlete a ORDER BY a.birthDate DESC")
    List<Athlete> findAllOrderByBirthDateDesc();
}
