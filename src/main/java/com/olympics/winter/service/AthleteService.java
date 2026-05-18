package com.olympics.winter.service;

import com.olympics.winter.entity.Athlete;
import com.olympics.winter.entity.User;
import com.olympics.winter.exception.BusinessException;
import com.olympics.winter.exception.ResourceNotFoundException;
import com.olympics.winter.repository.AthleteRepository;
import com.olympics.winter.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final UserRepository userRepository;

    public AthleteService(AthleteRepository athleteRepository, UserRepository userRepository) {
        this.athleteRepository = athleteRepository;
        this.userRepository = userRepository;
    }

    public List<Athlete> findAll() {
        return athleteRepository.findAll();
    }

    public Athlete findById(Long id) {
        return athleteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + id));
    }

    public Athlete findByUserId(Long userId) {
        return athleteRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete profile not found for user id: " + userId));
    }

    public Athlete save(Athlete athlete) {
        return athleteRepository.save(athlete);
    }

    public Athlete update(Long id, Athlete updated, Long requestingUserId) {
        Athlete existing = findById(id);

        if (existing.getUser() != null && !existing.getUser().getId().equals(requestingUserId)) {
            User requestingUser = userRepository.findById(requestingUserId).orElseThrow();
            if (requestingUser.getRole() != User.Role.ADMIN) {
                throw new BusinessException("You can only update your own profile");
            }
        }

        existing.setName(updated.getName());
        existing.setCountry(updated.getCountry());
        existing.setBirthDate(updated.getBirthDate());
        return athleteRepository.save(existing);
    }

    public void delete(Long id, Long requestingUserId) {
        Athlete athlete = findById(id);

        if (athlete.getUser() != null && !athlete.getUser().getId().equals(requestingUserId)) {
            User requestingUser = userRepository.findById(requestingUserId).orElseThrow();
            if (requestingUser.getRole() != User.Role.ADMIN) {
                throw new BusinessException("You can only delete your own profile");
            }
        }

        athleteRepository.delete(athlete);
    }

    public void deleteAsAdmin(Long id) {
        Athlete athlete = findById(id);
        athleteRepository.delete(athlete);
    }
}
