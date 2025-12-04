package com.example.mma.config;

import com.example.mma.entity.*;
import com.example.mma.enums.*;
import com.example.mma.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            FighterRepository fighterRepository,
            RuleSetRepository ruleSetRepository,
            EventRepository eventRepository,
            BoutRepository boutRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // ==================== USUARIOS ====================
            // Admin principal
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", passwordEncoder.encode("admin123"), "Administrador Principal", UserRole.ADMIN);
                admin.setEmail("admin@mmalive.com");
                userRepository.save(admin);
            }

            // Jueces
            if (!userRepository.existsByUsername("juez1")) {
                User judge1 = new User("juez1", passwordEncoder.encode("juez123"), "Carlos García", UserRole.JUDGE);
                judge1.setEmail("carlos.garcia@mmalive.com");
                userRepository.save(judge1);
            }

            if (!userRepository.existsByUsername("juez2")) {
                User judge2 = new User("juez2", passwordEncoder.encode("juez123"), "María López", UserRole.JUDGE);
                judge2.setEmail("maria.lopez@mmalive.com");
                userRepository.save(judge2);
            }

            if (!userRepository.existsByUsername("juez3")) {
                User judge3 = new User("juez3", passwordEncoder.encode("juez123"), "Roberto Sánchez", UserRole.JUDGE);
                judge3.setEmail("roberto.sanchez@mmalive.com");
                userRepository.save(judge3);
            }

            // Supervisor
            if (!userRepository.existsByUsername("supervisor")) {
                User supervisor = new User("supervisor", passwordEncoder.encode("super123"), "Ana Martínez", UserRole.SUPERVISOR);
                supervisor.setEmail("ana.martinez@mmalive.com");
                userRepository.save(supervisor);
            }

            // ==================== REGLAS ====================
            RuleSet professionalRules = ruleSetRepository.findByName("UFC Professional").orElseGet(() -> {
                RuleSet rules = new RuleSet("UFC Professional", 300, 60, 3);
                rules.setDescription("Reglas profesionales estándar UFC");
                rules.setMaxRounds(5);
                rules.setAllowDraws(false);
                rules.setTenPointMustSystem(true);
                return ruleSetRepository.save(rules);
            });

            RuleSet amateurRules = ruleSetRepository.findByName("Amateur MMA").orElseGet(() -> {
                RuleSet rules = new RuleSet("Amateur MMA", 180, 60, 3);
                rules.setDescription("Reglas para competiciones amateur");
                rules.setMaxRounds(3);
                rules.setAllowDraws(true);
                return ruleSetRepository.save(rules);
            });

            // ==================== PELEADORES ====================
            Fighter fighter1 = fighterRepository.findById(1L).orElseGet(() -> {
                Fighter f = new Fighter("Conor", "McGregor", WeightCategory.Lightweight);
                f.setClub("SBG Ireland");
                f.setRecordW(22); f.setRecordL(6); f.setRecordD(0);
                return fighterRepository.save(f);
            });

            Fighter fighter2 = fighterRepository.findById(2L).orElseGet(() -> {
                Fighter f = new Fighter("Khabib", "Nurmagomedov", WeightCategory.Lightweight);
                f.setClub("AKA");
                f.setRecordW(29); f.setRecordL(0); f.setRecordD(0);
                f.setStatus(FighterStatus.Retired);
                return fighterRepository.save(f);
            });

            Fighter fighter3 = fighterRepository.findById(3L).orElseGet(() -> {
                Fighter f = new Fighter("Israel", "Adesanya", WeightCategory.Middleweight);
                f.setClub("City Kickboxing");
                f.setRecordW(24); f.setRecordL(3); f.setRecordD(0);
                return fighterRepository.save(f);
            });

            Fighter fighter4 = fighterRepository.findById(4L).orElseGet(() -> {
                Fighter f = new Fighter("Alex", "Pereira", WeightCategory.Middleweight);
                f.setClub("Glover Teixeira MMA");
                f.setRecordW(9); f.setRecordL(2); f.setRecordD(0);
                return fighterRepository.save(f);
            });

            Fighter fighter5 = fighterRepository.findById(5L).orElseGet(() -> {
                Fighter f = new Fighter("Jon", "Jones", WeightCategory.Heavyweight);
                f.setClub("Jackson Wink MMA");
                f.setRecordW(27); f.setRecordL(1); f.setRecordD(0);
                return fighterRepository.save(f);
            });

            Fighter fighter6 = fighterRepository.findById(6L).orElseGet(() -> {
                Fighter f = new Fighter("Stipe", "Miocic", WeightCategory.Heavyweight);
                f.setClub("Strong Style MMA");
                f.setRecordW(20); f.setRecordL(4); f.setRecordD(0);
                return fighterRepository.save(f);
            });

            Fighter fighter7 = fighterRepository.findById(7L).orElseGet(() -> {
                Fighter f = new Fighter("Max", "Holloway", WeightCategory.Featherweight);
                f.setClub("Gracie Technics");
                f.setRecordW(26); f.setRecordL(7); f.setRecordD(0);
                return fighterRepository.save(f);
            });

            Fighter fighter8 = fighterRepository.findById(8L).orElseGet(() -> {
                Fighter f = new Fighter("Alexander", "Volkanovski", WeightCategory.Featherweight);
                f.setClub("City Kickboxing");
                f.setRecordW(26); f.setRecordL(3); f.setRecordD(0);
                return fighterRepository.save(f);
            });

            // ==================== EVENTO ====================
            Event event = eventRepository.findById(1L).orElseGet(() -> {
                Event e = new Event("MMA Championship Night", LocalDateTime.now().plusDays(7), EventType.Professional);
                e.setDescription("Evento de campeonato profesional");
                e.setLocation("Arena Principal");
                e.setRuleSet(professionalRules);
                return eventRepository.save(e);
            });

            // ==================== PELEAS ====================
            if (boutRepository.count() == 0) {
                Bout bout1 = new Bout(fighter3, fighter4, 5);
                bout1.setEvent(event);
                bout1.setBoutNumber(1);
                bout1.setState(BoutState.Programada);
                bout1.setScheduledAt(LocalDateTime.now().plusDays(7));
                boutRepository.save(bout1);

                Bout bout2 = new Bout(fighter5, fighter6, 5);
                bout2.setEvent(event);
                bout2.setBoutNumber(2);
                bout2.setState(BoutState.Programada);
                bout2.setScheduledAt(LocalDateTime.now().plusDays(7).plusHours(1));
                boutRepository.save(bout2);

                Bout bout3 = new Bout(fighter7, fighter8, 3);
                bout3.setEvent(event);
                bout3.setBoutNumber(3);
                bout3.setState(BoutState.Programada);
                bout3.setScheduledAt(LocalDateTime.now().plusDays(7).plusHours(2));
                boutRepository.save(bout3);
            }

            System.out.println("╔══════════════════════════════════════════════════════════╗");
            System.out.println("║         🥊 MMA LIVE - Sistema Iniciado 🥊                ║");
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            System.out.println("║  ✅ Usuarios creados:                                    ║");
            System.out.println("║     • admin / admin123 (Administrador)                   ║");
            System.out.println("║     • juez1, juez2, juez3 / juez123 (Jueces)             ║");
            System.out.println("║     • supervisor / super123 (Supervisor)                 ║");
            System.out.println("║  ✅ " + fighterRepository.count() + " peleadores registrados                          ║");
            System.out.println("║  ✅ " + boutRepository.count() + " peleas programadas                              ║");
            System.out.println("║  ✅ " + ruleSetRepository.count() + " conjuntos de reglas                             ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
        };
    }
}
