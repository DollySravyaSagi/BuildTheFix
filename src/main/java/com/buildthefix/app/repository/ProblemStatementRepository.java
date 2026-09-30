package com.buildthefix.app.repository;

import com.buildthefix.app.entity.ProblemStatus;
import com.buildthefix.app.entity.ProblemStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProblemStatementRepository extends JpaRepository<ProblemStatement, Long> {
    List<ProblemStatement> findByStatusOrderByCreatedAtDesc(ProblemStatus status);
    List<ProblemStatement> findAllByOrderByCreatedAtDesc();

    long countByStatus(ProblemStatus status);

    @Query("SELECT p FROM ProblemStatement p WHERE " +
           "LOWER(p.rawTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.rawDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.formalTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.targetPersona) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ProblemStatement> searchByKeyword(@Param("keyword") String keyword);
}
