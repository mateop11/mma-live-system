package com.example.mma.repository;

import com.example.mma.entity.RuleSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleSetRepository extends JpaRepository<RuleSet, Long> {
    Optional<RuleSet> findByName(String name);
}

