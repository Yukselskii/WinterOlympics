package com.olympics.winter.controller;

import com.olympics.winter.dto.RegistrationRequest;
import com.olympics.winter.entity.*;
import com.olympics.winter.repository.UserRepository;
import com.olympics.winter.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class WebController {

    private final CompetitionService competitionService;
    private final AthleteService athleteService;
    private final StatisticsService statisticsService;
    private final UserService userService;
    private final UserRepository userRepository;

    public WebController(CompetitionService competitionService,
                          AthleteService athleteService,
                          StatisticsService statisticsService,
                          UserService userService,
                          UserRepository userRepository) {
        this.competitionService = competitionService;
        this.athleteService = athleteService;
        this.statisticsService = statisticsService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("competitions", competitionService.findAll());
        model.addAttribute("medalTable", statisticsService.getMedalTable());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String name,
                                  @RequestParam String country,
                                  @RequestParam String gender,
                                  @RequestParam String birthDate,
                                  RedirectAttributes redirectAttributes) {
        try {
            RegistrationRequest req = new RegistrationRequest();
            req.setUsername(username);
            req.setPassword(password);
            req.setName(name);
            req.setCountry(country);
            req.setGender(gender);
            req.setBirthDate(birthDate);
            userService.register(req);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("competitions", competitionService.findAll());
        if (user.getRole() == User.Role.ATHLETE && user.getAthlete() != null) {
            model.addAttribute("athlete", user.getAthlete());
        }
        return "dashboard";
    }

    @GetMapping("/competitions")
    public String competitions(Model model) {
        model.addAttribute("competitions", competitionService.findAll());
        return "competitions";
    }

    @GetMapping("/competitions/{id}")
    public String competitionDetail(@PathVariable Long id, Model model) {
        Competition competition = competitionService.findById(id);
        model.addAttribute("competition", competition);
        model.addAttribute("registrations", competitionService.getRegistrations(id));

        if (competition.getType() == Competition.CompetitionType.SKI_SLALOM) {
            model.addAttribute("rankings", competitionService.getSlalomRankings(id));
            model.addAttribute("allResults", competitionService.getSlalomResults(id));
        } else {
            model.addAttribute("rankings", competitionService.getBiathlonRankings(id));
            model.addAttribute("allResults", competitionService.getBiathlonResults(id));
        }
        return "competition-detail";
    }

    @PostMapping("/competitions/{id}/register")
    public String registerForCompetition(@PathVariable Long id,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            if (user.getAthlete() == null) {
                redirectAttributes.addFlashAttribute("error", "No athlete profile found");
                return "redirect:/competitions/" + id;
            }
            competitionService.registerAthlete(id, user.getAthlete().getId());
            redirectAttributes.addFlashAttribute("success", "Successfully registered!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/competitions/" + id;
    }

    @GetMapping("/athletes")
    public String athletes(Model model) {
        model.addAttribute("athletes", athleteService.findAll());
        return "athletes";
    }

    @GetMapping("/athletes/{id}")
    public String athleteDetail(@PathVariable Long id, Model model) {
        model.addAttribute("athlete", athleteService.findById(id));
        return "athlete-detail";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        if (user.getAthlete() != null) {
            model.addAttribute("athlete", user.getAthlete());
        }
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String name,
                                 @RequestParam String country,
                                 @RequestParam String birthDate,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            if (user.getAthlete() != null) {
                Athlete updated = new Athlete();
                updated.setName(name);
                updated.setCountry(country);
                updated.setBirthDate(LocalDate.parse(birthDate));
                athleteService.update(user.getAthlete().getId(), updated, user.getId());
                redirectAttributes.addFlashAttribute("success", "Profile updated!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(@AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            if (user.getAthlete() != null) {
                athleteService.delete(user.getAthlete().getId(), user.getId());
            }
            return "redirect:/logout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile";
        }
    }

    @GetMapping("/medals")
    public String medalTable(Model model) {
        model.addAttribute("medalTable", statisticsService.getMedalTable());
        return "medals";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        model.addAttribute("avgAge", String.format("%.1f", statisticsService.getAverageAthleteAge()));
        statisticsService.getYoungestMedalist().ifPresent(a -> model.addAttribute("youngest", a));
        statisticsService.getOldestMedalist().ifPresent(a -> model.addAttribute("oldest", a));
        return "statistics";
    }

    // Admin pages

    @GetMapping("/admin/competitions/new")
    public String newCompetitionForm(Model model) {
        return "admin/competition-form";
    }

    @PostMapping("/admin/competitions/new")
    public String createCompetition(@RequestParam String name,
                                     @RequestParam String type,
                                     @RequestParam String gender,
                                     @RequestParam int minimumAge,
                                     @RequestParam String competitionDate,
                                     @RequestParam(defaultValue = "30") int topCutoff,
                                     RedirectAttributes redirectAttributes) {
        try {
            Competition c = new Competition();
            c.setName(name);
            c.setType(Competition.CompetitionType.valueOf(type));
            c.setGender(Athlete.Gender.valueOf(gender));
            c.setMinimumAge(minimumAge);
            c.setCompetitionDate(LocalDate.parse(competitionDate));
            c.setTopCutoff(topCutoff);
            competitionService.save(c);
            redirectAttributes.addFlashAttribute("success", "Competition created!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/competitions";
    }

    @GetMapping("/admin/competitions/{id}/results")
    public String resultsForm(@PathVariable Long id, Model model) {
        Competition competition = competitionService.findById(id);
        model.addAttribute("competition", competition);
        model.addAttribute("registrations", competitionService.getRegistrations(id));
        if (competition.getType() == Competition.CompetitionType.SKI_SLALOM) {
            model.addAttribute("results", competitionService.getSlalomResults(id));
        } else {
            model.addAttribute("results", competitionService.getBiathlonResults(id));
        }
        return "admin/results-form";
    }

    @PostMapping("/admin/competitions/{id}/results/slalom/first-run")
    public String submitFirstRun(@PathVariable Long id,
                                  @RequestParam Long athleteId,
                                  @RequestParam(required = false) Double time,
                                  @RequestParam(defaultValue = "false") boolean dnf,
                                  RedirectAttributes redirectAttributes) {
        try {
            competitionService.enterFirstRunResult(id, athleteId, time, dnf);
            redirectAttributes.addFlashAttribute("success", "First run result saved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/competitions/" + id + "/results";
    }

    @PostMapping("/admin/competitions/{id}/finalize/first-run")
    public String finalizeFirstRun(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            competitionService.finalizeFirstRun(id);
            redirectAttributes.addFlashAttribute("success", "First run finalized! Top athletes advanced.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/competitions/" + id + "/results";
    }

    @PostMapping("/admin/competitions/{id}/results/slalom/second-run")
    public String submitSecondRun(@PathVariable Long id,
                                   @RequestParam Long athleteId,
                                   @RequestParam(required = false) Double time,
                                   @RequestParam(defaultValue = "false") boolean dnf,
                                   RedirectAttributes redirectAttributes) {
        try {
            competitionService.enterSecondRunResult(id, athleteId, time, dnf);
            redirectAttributes.addFlashAttribute("success", "Second run result saved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/competitions/" + id + "/results";
    }

    @PostMapping("/admin/competitions/{id}/finalize/slalom")
    public String finalizeSlalom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            competitionService.finalizeSlalomRankings(id);
            redirectAttributes.addFlashAttribute("success", "Slalom finalized! Medals assigned.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/competitions/" + id;
    }

    @PostMapping("/admin/competitions/{id}/results/biathlon")
    public String submitBiathlonResult(@PathVariable Long id,
                                        @RequestParam Long athleteId,
                                        @RequestParam(required = false) Double skiingTime,
                                        @RequestParam(defaultValue = "0") int missedShots,
                                        @RequestParam(defaultValue = "false") boolean dnf,
                                        RedirectAttributes redirectAttributes) {
        try {
            competitionService.enterBiathlonResult(id, athleteId, skiingTime, missedShots, dnf);
            redirectAttributes.addFlashAttribute("success", "Biathlon result saved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/competitions/" + id + "/results";
    }

    @PostMapping("/admin/competitions/{id}/finalize/biathlon")
    public String finalizeBiathlon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            competitionService.finalizeBiathlonRankings(id);
            redirectAttributes.addFlashAttribute("success", "Biathlon finalized! Medals assigned.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/competitions/" + id;
    }

    @GetMapping("/admin/athletes")
    public String adminAthletes(Model model) {
        model.addAttribute("athletes", athleteService.findAll());
        return "admin/athletes";
    }

    @GetMapping("/admin/athletes/{id}/edit")
    public String adminAthleteEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("athlete", athleteService.findById(id));
        return "admin/athlete-edit";
    }

    @PostMapping("/admin/athletes/{id}/edit")
    public String adminAthleteUpdate(@PathVariable Long id,
                                     @RequestParam String name,
                                     @RequestParam String country,
                                     @RequestParam String gender,
                                     @RequestParam String birthDate,
                                     RedirectAttributes redirectAttributes) {
        try {
            Athlete updated = new Athlete();
            updated.setName(name);
            updated.setCountry(country);
            updated.setGender(Athlete.Gender.valueOf(gender));
            updated.setBirthDate(LocalDate.parse(birthDate));
            athleteService.updateAsAdmin(id, updated);
            redirectAttributes.addFlashAttribute("success", "Athlete updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/athletes";
    }

    @PostMapping("/admin/athletes/{id}/delete")
    public String adminAthleteDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            athleteService.deleteAsAdmin(id);
            redirectAttributes.addFlashAttribute("success", "Athlete deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/athletes";
    }
}
