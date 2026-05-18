package com.olympics.winter.service;

import com.olympics.winter.entity.*;
import com.olympics.winter.exception.BusinessException;
import com.olympics.winter.exception.ResourceNotFoundException;
import com.olympics.winter.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@Transactional
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final CompetitionRegistrationRepository registrationRepository;
    private final AthleteRepository athleteRepository;
    private final SkiSlalomResultRepository skiSlalomResultRepository;
    private final BiathlonResultRepository biathlonResultRepository;
    private final MedalRepository medalRepository;

    public CompetitionService(CompetitionRepository competitionRepository,
                               CompetitionRegistrationRepository registrationRepository,
                               AthleteRepository athleteRepository,
                               SkiSlalomResultRepository skiSlalomResultRepository,
                               BiathlonResultRepository biathlonResultRepository,
                               MedalRepository medalRepository) {
        this.competitionRepository = competitionRepository;
        this.registrationRepository = registrationRepository;
        this.athleteRepository = athleteRepository;
        this.skiSlalomResultRepository = skiSlalomResultRepository;
        this.biathlonResultRepository = biathlonResultRepository;
        this.medalRepository = medalRepository;
    }

    public List<Competition> findAll() {
        return competitionRepository.findAll();
    }

    public Competition findById(Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found with id: " + id));
    }

    public Competition save(Competition competition) {
        return competitionRepository.save(competition);
    }

    public Competition update(Long id, Competition updated) {
        Competition existing = findById(id);
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setGender(updated.getGender());
        existing.setMinimumAge(updated.getMinimumAge());
        existing.setCompetitionDate(updated.getCompetitionDate());
        existing.setTopCutoff(updated.getTopCutoff());
        return competitionRepository.save(existing);
    }

    public void delete(Long id) {
        competitionRepository.delete(findById(id));
    }

    public void registerAthlete(Long competitionId, Long athleteId) {
        Competition competition = findById(competitionId);
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));

        if (competition.getStatus() != Competition.CompetitionStatus.UPCOMING) {
            throw new BusinessException("Registration is closed for this competition");
        }
        if (athlete.getGender() != competition.getGender()) {
            throw new BusinessException("Athlete gender does not match competition gender category");
        }

        int age = Period.between(athlete.getBirthDate(), LocalDate.now()).getYears();
        if (age < competition.getMinimumAge()) {
            throw new BusinessException("Athlete does not meet minimum age requirement of " + competition.getMinimumAge());
        }
        if (registrationRepository.existsByAthleteIdAndCompetitionId(athleteId, competitionId)) {
            throw new BusinessException("Athlete is already registered for this competition");
        }

        registrationRepository.save(new CompetitionRegistration(athlete, competition));
    }

    public void unregisterAthlete(Long competitionId, Long athleteId) {
        CompetitionRegistration reg = registrationRepository
                .findByAthleteIdAndCompetitionId(athleteId, competitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        registrationRepository.delete(reg);
    }

    public List<CompetitionRegistration> getRegistrations(Long competitionId) {
        return registrationRepository.findByCompetitionId(competitionId);
    }

    // ---- Ski Slalom ----

    public SkiSlalomResult enterFirstRunResult(Long competitionId, Long athleteId, Double time, boolean dnf) {
        Competition competition = findById(competitionId);
        if (competition.getType() != Competition.CompetitionType.SKI_SLALOM) {
            throw new BusinessException("Competition is not a ski slalom event");
        }
        if (competition.getStatus() != Competition.CompetitionStatus.UPCOMING) {
            throw new BusinessException("First run results can only be entered for upcoming competitions");
        }

        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));

        SkiSlalomResult result = skiSlalomResultRepository
                .findByCompetitionIdAndAthleteId(competitionId, athleteId)
                .orElse(new SkiSlalomResult());

        result.setCompetition(competition);
        result.setAthlete(athlete);
        result.setFirstRunTime(dnf ? null : time);
        result.setFirstRunDnf(dnf);

        return skiSlalomResultRepository.save(result);
    }

    public void finalizeFirstRun(Long competitionId) {
        Competition competition = findById(competitionId);
        if (competition.getType() != Competition.CompetitionType.SKI_SLALOM) {
            throw new BusinessException("Not a ski slalom competition");
        }

        List<SkiSlalomResult> finishers = skiSlalomResultRepository.findFinishersOrderByFirstRunTime(competitionId);
        int cutoff = competition.getTopCutoff() != null ? competition.getTopCutoff() : 30;

        for (int i = 0; i < finishers.size(); i++) {
            SkiSlalomResult r = finishers.get(i);
            r.setAdvancedToSecondRun(i < cutoff);
            skiSlalomResultRepository.save(r);
        }

        competition.setStatus(Competition.CompetitionStatus.FIRST_RUN_DONE);
        competitionRepository.save(competition);
    }

    public SkiSlalomResult enterSecondRunResult(Long competitionId, Long athleteId, Double time, boolean dnf) {
        Competition competition = findById(competitionId);
        if (competition.getStatus() != Competition.CompetitionStatus.FIRST_RUN_DONE) {
            throw new BusinessException("Second run can only be entered after first run is finalized");
        }

        SkiSlalomResult result = skiSlalomResultRepository
                .findByCompetitionIdAndAthleteId(competitionId, athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("No first run result found for this athlete"));

        if (!result.isAdvancedToSecondRun()) {
            throw new BusinessException("Athlete did not advance to second run");
        }

        result.setSecondRunTime(dnf ? null : time);
        result.setSecondRunDnf(dnf);
        result.calculateFinalTime();

        return skiSlalomResultRepository.save(result);
    }

    public List<SkiSlalomResult> finalizeSlalomRankings(Long competitionId) {
        Competition competition = findById(competitionId);

        List<SkiSlalomResult> ranked = skiSlalomResultRepository.findFinalRankings(competitionId);
        for (int i = 0; i < ranked.size(); i++) {
            ranked.get(i).setFinalRank(i + 1);
            skiSlalomResultRepository.save(ranked.get(i));
        }

        List<SkiSlalomResult> topThree = ranked.stream().filter(r -> r.getFinalRank() <= 3).toList();
        assignSlalomMedals(competition, topThree);

        competition.setStatus(Competition.CompetitionStatus.COMPLETED);
        competitionRepository.save(competition);
        return ranked;
    }

    public List<SkiSlalomResult> getSlalomResults(Long competitionId) {
        return skiSlalomResultRepository.findByCompetitionId(competitionId);
    }

    public List<SkiSlalomResult> getSlalomRankings(Long competitionId) {
        return skiSlalomResultRepository.findFinalRankings(competitionId);
    }

    // ---- Biathlon ----

    public BiathlonResult enterBiathlonResult(Long competitionId, Long athleteId, Double skiingTime, int missedShots, boolean dnf) {
        Competition competition = findById(competitionId);
        if (competition.getType() != Competition.CompetitionType.BIATHLON) {
            throw new BusinessException("Competition is not a biathlon event");
        }

        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));

        BiathlonResult result = biathlonResultRepository
                .findByCompetitionIdAndAthleteId(competitionId, athleteId)
                .orElse(new BiathlonResult());

        result.setCompetition(competition);
        result.setAthlete(athlete);
        result.setSkiingTime(dnf ? null : skiingTime);
        result.setMissedShots(missedShots);
        result.setDnf(dnf);
        result.calculateFinalTime();

        return biathlonResultRepository.save(result);
    }

    public List<BiathlonResult> finalizeBiathlonRankings(Long competitionId) {
        Competition competition = findById(competitionId);

        List<BiathlonResult> ranked = biathlonResultRepository.findFinalRankings(competitionId);
        for (int i = 0; i < ranked.size(); i++) {
            ranked.get(i).setFinalRank(i + 1);
            biathlonResultRepository.save(ranked.get(i));
        }

        List<BiathlonResult> topThree = ranked.stream().filter(r -> r.getFinalRank() <= 3).toList();
        assignBiathlonMedals(competition, topThree);

        competition.setStatus(Competition.CompetitionStatus.COMPLETED);
        competitionRepository.save(competition);
        return ranked;
    }

    public List<BiathlonResult> getBiathlonResults(Long competitionId) {
        return biathlonResultRepository.findByCompetitionId(competitionId);
    }

    public List<BiathlonResult> getBiathlonRankings(Long competitionId) {
        return biathlonResultRepository.findFinalRankings(competitionId);
    }

    private void assignSlalomMedals(Competition competition, List<SkiSlalomResult> topResults) {
        medalRepository.deleteAll(medalRepository.findByCompetitionId(competition.getId()));
        Medal.MedalType[] types = {Medal.MedalType.GOLD, Medal.MedalType.SILVER, Medal.MedalType.BRONZE};
        for (int i = 0; i < Math.min(3, topResults.size()); i++) {
            medalRepository.save(new Medal(competition, topResults.get(i).getAthlete(), types[i]));
        }
    }

    private void assignBiathlonMedals(Competition competition, List<BiathlonResult> topResults) {
        medalRepository.deleteAll(medalRepository.findByCompetitionId(competition.getId()));
        Medal.MedalType[] types = {Medal.MedalType.GOLD, Medal.MedalType.SILVER, Medal.MedalType.BRONZE};
        for (int i = 0; i < Math.min(3, topResults.size()); i++) {
            medalRepository.save(new Medal(competition, topResults.get(i).getAthlete(), types[i]));
        }
    }
}
