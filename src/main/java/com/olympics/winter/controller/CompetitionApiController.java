package com.olympics.winter.controller;

import com.olympics.winter.dto.BiathlonResultRequest;
import com.olympics.winter.dto.SkiSlalomResultRequest;
import com.olympics.winter.entity.*;
import com.olympics.winter.repository.UserRepository;
import com.olympics.winter.service.CompetitionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionApiController {

    private final CompetitionService competitionService;
    private final UserRepository userRepository;

    public CompetitionApiController(CompetitionService competitionService, UserRepository userRepository) {
        this.competitionService = competitionService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Competition> getAll() {
        return competitionService.findAll();
    }

    @GetMapping("/{id}")
    public Competition getById(@PathVariable Long id) {
        return competitionService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Competition> create(@RequestBody Competition competition) {
        return ResponseEntity.status(HttpStatus.CREATED).body(competitionService.save(competition));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Competition update(@PathVariable Long id, @RequestBody Competition competition) {
        return competitionService.update(id, competition);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        competitionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/registrations")
    public List<CompetitionRegistration> getRegistrations(@PathVariable Long id) {
        return competitionService.getRegistrations(id);
    }

    @PostMapping("/{id}/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> register(@PathVariable Long id,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Long athleteId = user.getAthlete() != null ? user.getAthlete().getId() : null;
        if (athleteId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No athlete profile found"));
        }
        competitionService.registerAthlete(id, athleteId);
        return ResponseEntity.ok(Map.of("message", "Successfully registered"));
    }

    @PostMapping("/{id}/register/{athleteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> registerAthlete(@PathVariable Long id, @PathVariable Long athleteId) {
        competitionService.registerAthlete(id, athleteId);
        return ResponseEntity.ok(Map.of("message", "Athlete registered successfully"));
    }

    @DeleteMapping("/{id}/register/{athleteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unregister(@PathVariable Long id, @PathVariable Long athleteId) {
        competitionService.unregisterAthlete(id, athleteId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/results/slalom/first-run")
    @PreAuthorize("hasRole('ADMIN')")
    public SkiSlalomResult enterFirstRun(@PathVariable Long id, @RequestBody SkiSlalomResultRequest req) {
        return competitionService.enterFirstRunResult(id, req.getAthleteId(), req.getFirstRunTime(), req.isFirstRunDnf());
    }

    @PostMapping("/{id}/finalize/slalom/first-run")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> finalizeFirstRun(@PathVariable Long id) {
        competitionService.finalizeFirstRun(id);
        return ResponseEntity.ok(Map.of("message", "First run finalized"));
    }

    @PostMapping("/{id}/results/slalom/second-run")
    @PreAuthorize("hasRole('ADMIN')")
    public SkiSlalomResult enterSecondRun(@PathVariable Long id, @RequestBody SkiSlalomResultRequest req) {
        return competitionService.enterSecondRunResult(id, req.getAthleteId(), req.getSecondRunTime(), req.isSecondRunDnf());
    }

    @PostMapping("/{id}/finalize/slalom")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SkiSlalomResult> finalizeSlalom(@PathVariable Long id) {
        return competitionService.finalizeSlalomRankings(id);
    }

    @GetMapping("/{id}/rankings/slalom")
    public List<SkiSlalomResult> getSlalomRankings(@PathVariable Long id) {
        return competitionService.getSlalomRankings(id);
    }

    @PostMapping("/{id}/results/biathlon")
    @PreAuthorize("hasRole('ADMIN')")
    public BiathlonResult enterBiathlonResult(@PathVariable Long id, @RequestBody BiathlonResultRequest req) {
        return competitionService.enterBiathlonResult(id, req.getAthleteId(), req.getSkiingTime(), req.getMissedShots(), req.isDnf());
    }

    @PostMapping("/{id}/finalize/biathlon")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BiathlonResult> finalizeBiathlon(@PathVariable Long id) {
        return competitionService.finalizeBiathlonRankings(id);
    }

    @GetMapping("/{id}/rankings/biathlon")
    public List<BiathlonResult> getBiathlonRankings(@PathVariable Long id) {
        return competitionService.getBiathlonRankings(id);
    }
}
