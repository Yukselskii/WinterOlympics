package com.olympics.winter.service;

import com.olympics.winter.dto.RegistrationRequest;
import com.olympics.winter.entity.Athlete;
import com.olympics.winter.entity.User;
import com.olympics.winter.exception.BusinessException;
import com.olympics.winter.repository.AthleteRepository;
import com.olympics.winter.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AthleteRepository athleteRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AthleteRepository athleteRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.athleteRepository = athleteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already taken: " + request.getUsername());
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                User.Role.ATHLETE
        );
        userRepository.save(user);

        Athlete athlete = new Athlete(
                request.getName(),
                request.getCountry(),
                Athlete.Gender.valueOf(request.getGender().toUpperCase()),
                LocalDate.parse(request.getBirthDate())
        );
        athlete.setUser(user);
        user.setAthlete(athlete);
        athleteRepository.save(athlete);

        return user;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found: " + username));
    }
}
