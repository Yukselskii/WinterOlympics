package com.olympics.winter;

import com.olympics.winter.entity.Athlete;
import com.olympics.winter.entity.Competition;
import com.olympics.winter.entity.User;
import com.olympics.winter.repository.AthleteRepository;
import com.olympics.winter.repository.CompetitionRepository;
import com.olympics.winter.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AthleteRepository athleteRepository;
    private final CompetitionRepository competitionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, AthleteRepository athleteRepository,
                       CompetitionRepository competitionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.athleteRepository = athleteRepository;
        this.competitionRepository = competitionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        User admin = new User("admin", passwordEncoder.encode("admin123"), User.Role.ADMIN);
        userRepository.save(admin);

        User u1 = new User("mikaela", passwordEncoder.encode("password"), User.Role.ATHLETE);
        userRepository.save(u1);
        Athlete a1 = new Athlete("Mikaela Shiffrin", "USA", Athlete.Gender.FEMALE, LocalDate.of(1995, 3, 13));
        a1.setUser(u1);
        u1.setAthlete(a1);
        athleteRepository.save(a1);

        User u2 = new User("petra", passwordEncoder.encode("password"), User.Role.ATHLETE);
        userRepository.save(u2);
        Athlete a2 = new Athlete("Petra Vlhova", "SVK", Athlete.Gender.FEMALE, LocalDate.of(1995, 6, 13));
        a2.setUser(u2);
        u2.setAthlete(a2);
        athleteRepository.save(a2);

        User u3 = new User("henrik", passwordEncoder.encode("password"), User.Role.ATHLETE);
        userRepository.save(u3);
        Athlete a3 = new Athlete("Henrik Kristoffersen", "NOR", Athlete.Gender.MALE, LocalDate.of(1994, 1, 25));
        a3.setUser(u3);
        u3.setAthlete(a3);
        athleteRepository.save(a3);

        User u4 = new User("alexis", passwordEncoder.encode("password"), User.Role.ATHLETE);
        userRepository.save(u4);
        Athlete a4 = new Athlete("Alexis Pinturault", "FRA", Athlete.Gender.MALE, LocalDate.of(1991, 3, 20));
        a4.setUser(u4);
        u4.setAthlete(a4);
        athleteRepository.save(a4);

        athleteRepository.save(new Athlete("Johannes Klaebo", "NOR", Athlete.Gender.MALE, LocalDate.of(1996, 10, 22)));
        athleteRepository.save(new Athlete("Marte Olsbu Roeiseland", "NOR", Athlete.Gender.FEMALE, LocalDate.of(1990, 5, 4)));
        athleteRepository.save(new Athlete("Bianca Cherechigno", "ITA", Athlete.Gender.FEMALE, LocalDate.of(2002, 7, 19)));

        Competition c1 = new Competition("Women's Slalom", Competition.CompetitionType.SKI_SLALOM,
                Athlete.Gender.FEMALE, 16, LocalDate.of(2026, 2, 10));
        c1.setTopCutoff(30);
        competitionRepository.save(c1);

        Competition c2 = new Competition("Men's Slalom", Competition.CompetitionType.SKI_SLALOM,
                Athlete.Gender.MALE, 16, LocalDate.of(2026, 2, 12));
        c2.setTopCutoff(30);
        competitionRepository.save(c2);

        competitionRepository.save(new Competition("Women's Biathlon Sprint", Competition.CompetitionType.BIATHLON,
                Athlete.Gender.FEMALE, 18, LocalDate.of(2026, 2, 15)));

        competitionRepository.save(new Competition("Men's Biathlon Sprint", Competition.CompetitionType.BIATHLON,
                Athlete.Gender.MALE, 18, LocalDate.of(2026, 2, 17)));

        System.out.println("=== Sample data seeded ===");
        System.out.println("Admin: admin / admin123");
        System.out.println("Athletes: mikaela, petra, henrik, alexis / password");
    }
}
