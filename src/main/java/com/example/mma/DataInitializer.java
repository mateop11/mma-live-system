package com.example.mma;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(FighterRepository fighterRepo, FightRepository fightRepo) {
        return args -> {
            if (fighterRepo.count() == 0) {
                Fighter f1 = fighterRepo.save(new Fighter("Carlos", "Lopez", "Dojo Quito", "Lightweight", 5, 1, 0, "Active"));
                Fighter f2 = fighterRepo.save(new Fighter("Andrea", "Perez", "Team Guayas", "Featherweight", 3, 2, 0, "Active"));
                Fighter f3 = fighterRepo.save(new Fighter("Miguel", "Torres", "MMA Norte", "Welterweight", 7, 3, 1, "Injured"));

                if (fightRepo.count() == 0) {
                    fightRepo.save(new Fight(f1, f2, f2, 3, "Finished"));
                    fightRepo.save(new Fight(f2, f3, null, 3, "Scheduled"));
                }
            }
        };
    }
}
