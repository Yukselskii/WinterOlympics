package com.olympics.winter.controller;

import com.olympics.winter.entity.Athlete;
import com.olympics.winter.entity.User;
import com.olympics.winter.repository.UserRepository;
import com.olympics.winter.service.AthleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/athletes")
public class AthleteApiController {

    private final AthleteService athleteService;
    private final UserRepository userRepository;

    public AthleteApiController(AthleteService athleteService, UserRepository userRepository) {
        this.athleteService = athleteService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Athlete> getAll() {
        return athleteService.findAll();
    }

    @GetMapping("/{id}")
    public Athlete getById(@PathVariable Long id) {
        return athleteService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Athlete update(@PathVariable Long id, @RequestBody Athlete athlete,
                          @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return athleteService.update(id, athlete, user.getId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        if (user.getRole() == User.Role.ADMIN) {
            athleteService.deleteAsAdmin(id);
        } else {
            athleteService.delete(id, user.getId());
        }
        return ResponseEntity.noContent().build();
    }
}
