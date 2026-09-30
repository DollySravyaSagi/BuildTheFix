package com.buildthefix.app.service;

import com.buildthefix.app.dto.*;
import com.buildthefix.app.entity.*;
import com.buildthefix.app.exception.ResourceNotFoundException;
import com.buildthefix.app.repository.ProblemStatementRepository;
import com.buildthefix.app.repository.SolutionRepository;
import com.buildthefix.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing problem creation, AI blueprint reframing, duplicate checking, search, and platform statistics.
 */
@Service
public class ProblemService {

    private final ProblemStatementRepository problemRepository;
    private final SolutionRepository solutionRepository;
    private final UserRepository userRepository;
    private final AiReframingService aiReframingService;
    private final DuplicateDetector duplicateDetector;

    public ProblemService(ProblemStatementRepository problemRepository,
                          SolutionRepository solutionRepository,
                          UserRepository userRepository,
                          AiReframingService aiReframingService,
                          DuplicateDetector duplicateDetector) {
        this.problemRepository = problemRepository;
        this.solutionRepository = solutionRepository;
        this.userRepository = userRepository;
        this.aiReframingService = aiReframingService;
        this.duplicateDetector = duplicateDetector;
    }

    @Transactional
    public ProblemResponseDto createProblem(CreateProblemRequestDto dto) {
        // Step 1: Create Entity with Raw User Inputs
        ProblemStatement problem = new ProblemStatement(
                dto.getClientName(),
                dto.getClientEmail(),
                dto.getRawTitle(),
                dto.getRawDescription()
        );

        // Associate poster entity if user account exists
        userRepository.findByEmail(dto.getClientEmail())
                .filter(u -> u instanceof ProblemPoster)
                .ifPresent(p -> problem.setPoster((ProblemPoster) p));

        // Step 2: Invoke AI Reframing Service to generate structured SaaS blueprint
        ProblemBlueprintDto blueprint = aiReframingService.reframeProblem(dto.getRawTitle(), dto.getRawDescription());
        problem.setFormalTitle(blueprint.getFormalTitle());
        problem.setTargetPersona(blueprint.getTargetPersona());
        problem.setTechStack(blueprint.getTechStack());
        problem.setCoreFeatures(blueprint.getCoreFeatures());
        problem.setRoadmap(blueprint.getRoadmap());

        // Step 3: Check existing problems for duplicate topics
        List<ProblemStatement> existingProblems = problemRepository.findAll();
        DuplicateCheckDto dupCheck = duplicateDetector.checkForDuplicates(dto.getRawTitle(), dto.getRawDescription(), existingProblems);
        problem.setDuplicate(dupCheck.isDuplicate());
        problem.setSimilarProblemId(dupCheck.getSimilarProblemId());
        problem.setSimilarityReason(dupCheck.getSimilarityReason());

        // Step 4: Persist and Return Response DTO
        ProblemStatement saved = problemRepository.save(problem);
        return new ProblemResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ProblemResponseDto> getAllProblems(String status, String search) {
        List<ProblemStatement> problems;

        if (search != null && !search.isBlank()) {
            problems = problemRepository.searchByKeyword(search.trim());
        } else if (status != null && !status.equalsIgnoreCase("all")) {
            try {
                ProblemStatus enumStatus = ProblemStatus.valueOf(status.toUpperCase());
                problems = problemRepository.findByStatusOrderByCreatedAtDesc(enumStatus);
            } catch (IllegalArgumentException e) {
                problems = problemRepository.findAllByOrderByCreatedAtDesc();
            }
        } else {
            problems = problemRepository.findAllByOrderByCreatedAtDesc();
        }

        return problems.stream()
                .map(ProblemResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProblemResponseDto getProblemById(Long id) {
        ProblemStatement problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem statement not found with ID: " + id));
        return new ProblemResponseDto(problem);
    }

    @Transactional(readOnly = true)
    public ProblemStatsDto getPlatformStats() {
        long total = problemRepository.count();
        long solved = problemRepository.countByStatus(ProblemStatus.SOLVED);
        long inProgress = problemRepository.countByStatus(ProblemStatus.IN_PROGRESS);
        long open = problemRepository.countByStatus(ProblemStatus.OPEN);
        long unlocked = solutionRepository.countByIsUnlockedTrue();

        // Simulated MRR calculation ($49/month per solved solution)
        BigDecimal mrr = BigDecimal.valueOf(solved).multiply(new BigDecimal("49.00"));

        return new ProblemStatsDto(total, solved, inProgress, open, unlocked, mrr);
    }

    @Transactional
    public void seedInitialData() {
        if (problemRepository.count() > 0) {
            return;
        }

        // Demo Problem 1
        ProblemStatement p1 = new ProblemStatement(
                "Sarah Jenkins",
                "sarah@sweetdelights.com",
                "WhatsApp Bakery Orders",
                "I run a custom cake shop. Customers send me order details, reference pictures, and text requests on WhatsApp. I manually write them down on sticky notes and draw order calendar entries on a whiteboard. I constantly lose notes or misread handwriting, causing custom text spelling mistakes on cakes, and late deliveries."
        );
        p1.setFormalTitle("SaaS Order Calendar & Recipe Tracker");
        p1.setTargetPersona("Boutique Bakery Owners & Custom Cake Designers");
        p1.setTechStack(List.of("React.js", "Node.js", "Express", "MongoDB", "Cloudinary (Images)"));
        p1.setCoreFeatures(List.of(
                "Interactive Order Calendar: Visual tracking of booking limits per day.",
                "Visual Order Builder: Form mapping cake size, flavors, text inscriptions, and reference photo uploads.",
                "Automated WhatsApp Confirmation: Generate templated receipts to send back to clients for confirmation."
        ));
        p1.setRoadmap(List.of(
                "Phase 1: Setup React SPA with drag-and-drop calendar UI.",
                "Phase 2: Add MongoDB schema mapping orders with image references.",
                "Phase 3: Integrate Cloudinary for reference photo uploads.",
                "Phase 4: Design shareable order confirmation pages."
        ));
        p1.setStatus(ProblemStatus.SOLVED);

        ProblemStatement savedP1 = problemRepository.save(p1);

        Solution sol1 = new Solution(
                savedP1,
                "Leo Vance",
                "leodev",
                "https://sweetorders-bakery.vercel.app",
                new BigDecimal("49.00")
        );
        sol1.setFullAccessCodeUrl("https://github.com/leodev/sweetorders-bakery-full");
        solutionRepository.save(sol1);

        // Demo Problem 2
        ProblemStatement p2 = new ProblemStatement(
                "Marcus Brodie",
                "marcus@brodiebuilt.com",
                "Losing track of contractor timesheets",
                "I manage a small home renovation crew. My subcontractors text me their daily hours, or tell me in person, and I compile them into a master Excel file at the end of the month. I constantly misplace texts, forget oral reports, and contractors argue over hours, causing invoice delays and trust issues."
        );
        p2.setFormalTitle("Micro-SaaS Billable Hours Tracker");
        p2.setTargetPersona("Construction Foremen & Crew Subcontractors");
        p2.setTechStack(List.of("Vite + React", "Firebase Auth & Firestore", "Tailwind CSS"));
        p2.setCoreFeatures(List.of(
                "One-Tap Clock-in/Clock-out: GPS-stamped start and end times for renovation jobs.",
                "Real-Time Coordinator Board: Dashboard for foreman to view live crew logins.",
                "Dispute-Free PDF Invoicing: Export timesheets signed digitally at end of shift."
        ));
        p2.setRoadmap(List.of(
                "Phase 1: Build mobile-friendly shift punch cards.",
                "Phase 2: Implement contractor role-based authentication.",
                "Phase 3: Connect PDF export engine for payroll approval."
        ));
        p2.setStatus(ProblemStatus.IN_PROGRESS);
        problemRepository.save(p2);

        // Demo Problem 3
        ProblemStatement p3 = new ProblemStatement(
                "Dr. Amanda Zhao",
                "amanda@veterinarycare.org",
                "Manual Patient Appointment Reminders & Pet Med Schedules",
                "Our veterinary clinic staff spends 3 hours every morning calling pet owners to confirm appointments and remind them about post-surgery medication schedules. Patients frequently forget medicine timings, and no-shows waste our surgeon's time."
        );
        p3.setFormalTitle("Automated Pet Care & SMS Appointment Cadence Engine");
        p3.setTargetPersona("Veterinary Clinics, Animal Hospitals & Pet Parents");
        p3.setTechStack(List.of("Next.js", "Node.js", "Twilio API", "MongoDB Atlas"));
        p3.setCoreFeatures(List.of(
                "Two-Way SMS Confirmations: Automated text alerts that update calendar on reply.",
                "Medication Regimen Portal: Timed reminders sent to pet parents with dosage instructions.",
                "No-Show Predictive Scoring: Flags high-risk appointment slots for staff follow-up."
        ));
        p3.setRoadmap(List.of(
                "Phase 1: Build schedule calendar sync module.",
                "Phase 2: Integrate Twilio Programmable Messaging webhooks.",
                "Phase 3: Create prescription reminder timeline widget."
        ));
        p3.setStatus(ProblemStatus.OPEN);
        problemRepository.save(p3);
    }
}
