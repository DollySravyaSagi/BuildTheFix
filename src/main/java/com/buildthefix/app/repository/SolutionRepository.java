package com.buildthefix.app.repository;

import com.buildthefix.app.entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
    List<Solution> findByProblemStatementId(Long problemStatementId);
    long countByIsUnlockedTrue();
}
