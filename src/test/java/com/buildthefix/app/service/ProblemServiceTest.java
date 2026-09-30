package com.buildthefix.app.service;

import com.buildthefix.app.dto.*;
import com.buildthefix.app.entity.ProblemStatement;
import com.buildthefix.app.entity.ProblemStatus;
import com.buildthefix.app.exception.ResourceNotFoundException;
import com.buildthefix.app.repository.ProblemStatementRepository;
import com.buildthefix.app.repository.SolutionRepository;
import com.buildthefix.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProblemServiceTest {

    @Mock
    private ProblemStatementRepository problemRepository;

    @Mock
    private SolutionRepository solutionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AiReframingService aiReframingService;

    @Mock
    private DuplicateDetector duplicateDetector;

    @InjectMocks
    private ProblemService problemService;

    private CreateProblemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = new CreateProblemRequestDto(
                "Sarah Jenkins",
                "sarah@bakery.com",
                "WhatsApp Bakery Orders",
                "Sticky notes get lost causing cake spelling mistakes."
        );
    }

    @Test
    @DisplayName("Should create problem and reframe blueprint via AI service")
    void testCreateProblemSuccess() {
        ProblemBlueprintDto mockBlueprint = new ProblemBlueprintDto(
                "SaaS Order Calendar",
                "Boutique Bakers",
                List.of("React", "Java", "PostgreSQL"),
                List.of("Calendar UI"),
                List.of("Phase 1")
        );
        when(aiReframingService.reframeProblem(any(), any())).thenReturn(mockBlueprint);

        DuplicateCheckDto mockDup = new DuplicateCheckDto(false, null, "");
        when(duplicateDetector.checkForDuplicates(any(), any(), any())).thenReturn(mockDup);

        when(problemRepository.save(any(ProblemStatement.class))).thenAnswer(invocation -> {
            ProblemStatement ps = invocation.getArgument(0);
            ps.setId(10L);
            return ps;
        });

        ProblemResponseDto response = problemService.createProblem(requestDto);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("SaaS Order Calendar", response.getFormalTitle());
        assertFalse(response.isDuplicate());

        verify(aiReframingService, times(1)).reframeProblem(requestDto.getRawTitle(), requestDto.getRawDescription());
        verify(problemRepository, times(1)).save(any(ProblemStatement.class));
    }

    @Test
    @DisplayName("Should retrieve problem by ID or throw ResourceNotFoundException")
    void testGetProblemById() {
        ProblemStatement ps = new ProblemStatement("Sarah", "sarah@bakery.com", "Title", "Description");
        ps.setId(1L);

        when(problemRepository.findById(1L)).thenReturn(Optional.of(ps));

        ProblemResponseDto result = problemService.getProblemById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());

        when(problemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> problemService.getProblemById(99L));
    }

    @Test
    @DisplayName("Should calculate platform statistics accurately")
    void testGetPlatformStats() {
        when(problemRepository.count()).thenReturn(10L);
        when(problemRepository.countByStatus(ProblemStatus.SOLVED)).thenReturn(3L);
        when(problemRepository.countByStatus(ProblemStatus.IN_PROGRESS)).thenReturn(2L);
        when(problemRepository.countByStatus(ProblemStatus.OPEN)).thenReturn(5L);
        when(solutionRepository.countByIsUnlockedTrue()).thenReturn(2L);

        ProblemStatsDto stats = problemService.getPlatformStats();

        assertNotNull(stats);
        assertEquals(10L, stats.getTotalProblems());
        assertEquals(3L, stats.getSolvedProblems());
        assertEquals(5L, stats.getOpenProblems());
        // MRR = 3 solved * $49 = $147.00
        assertEquals(147.00, stats.getTotalMrr().doubleValue());
    }
}
