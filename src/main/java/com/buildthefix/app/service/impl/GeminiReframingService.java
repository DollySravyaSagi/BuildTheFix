package com.buildthefix.app.service.impl;

import com.buildthefix.app.dto.ProblemBlueprintDto;
import com.buildthefix.app.service.AiReframingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of AiReframingService that connects directly to the Google Gemini API using Java 17 HttpClient.
 * Rewrites plain-text user operational bottlenecks into structured SaaS technical blueprints.
 * Includes automatic fallback to dynamic domain synthesizer if API key is unconfigured or unavailable.
 */
@Service
public class GeminiReframingService implements AiReframingService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String apiUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GeminiReframingService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ProblemBlueprintDto reframeProblem(String rawTitle, String rawDescription) {
        if (apiKey != null && !apiKey.isBlank()) {
            try {
                return callGeminiApi(rawTitle, rawDescription);
            } catch (Exception ex) {
                System.err.println("Gemini API call error (falling back to dynamic synthesizer): " + ex.getMessage());
            }
        } else {
            System.out.println("GEMINI_API_KEY not configured. Using smart dynamic local blueprint synthesizer.");
        }

        return generateDynamicLocalBlueprint(rawTitle, rawDescription);
    }

    /**
     * Executes an HTTP POST request to Google Gemini API using native Java 17 java.net.http.HttpClient.
     */
    private ProblemBlueprintDto callGeminiApi(String rawTitle, String rawDescription) throws Exception {
        String fullUrl = apiUrl + "?key=" + apiKey.trim();

        String promptText = """
                You are an elite SaaS Solution Architect and CTO for the BuildTheFix marketplace.
                A user submitted the following operational bottleneck:
                Title: "%s"
                Description: "%s"

                Generate a high-value technical MVP blueprint to solve their specific bottleneck.
                Respond ONLY with a valid JSON object (no markdown, no backticks, no explanatory prose):
                {
                  "formal_title": "Clear, attractive SaaS/App name tailored specifically to their problem",
                  "target_persona": "Specific target customer persona",
                  "tech_stack": ["Frontend tech", "Backend tech", "Database", "Key API/Tool"],
                  "core_features": [
                    "Feature Title: Detailed feature description",
                    "Feature Title: Detailed feature description",
                    "Feature Title: Detailed feature description"
                  ],
                  "roadmap": [
                    "Phase 1: Step description",
                    "Phase 2: Step description",
                    "Phase 3: Step description"
                  ]
                }
                """.formatted(rawTitle.replace("\"", "'"), rawDescription.replace("\"", "'"));

        // Construct JSON Request Payload for Gemini API
        Map<String, Object> partMap = Map.of("text", promptText);
        Map<String, Object> contentsMap = Map.of("parts", List.of(partMap));
        Map<String, Object> requestBodyMap = Map.of("contents", List.of(contentsMap));

        String jsonPayload = objectMapper.writeValueAsString(requestBodyMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(15))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini API HTTP " + response.statusCode() + ": " + response.body());
        }

        // Parse Response Body
        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode candidates = rootNode.path("candidates");
        if (candidates.isArray() && candidates.size() > 0) {
            String textResponse = candidates.get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();

            // Clean Markdown markers if returned by AI
            String cleanJson = textResponse.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            JsonNode parsedBlueprint = objectMapper.readTree(cleanJson);

            String formalTitle = parsedBlueprint.path("formal_title").asText("Custom SaaS Solution");
            String targetPersona = parsedBlueprint.path("target_persona").asText("Small Business Owners");

            List<String> techStack = extractStringList(parsedBlueprint.path("tech_stack"), List.of("Next.js", "Java Spring Boot", "PostgreSQL"));
            List<String> coreFeatures = extractStringList(parsedBlueprint.path("core_features"), List.of("Automated workflow tracking", "Real-time notifications"));
            List<String> roadmap = extractStringList(parsedBlueprint.path("roadmap"), List.of("Phase 1: MVP Setup", "Phase 2: Integration", "Phase 3: Launch"));

            return new ProblemBlueprintDto(formalTitle, targetPersona, techStack, coreFeatures, roadmap);
        }

        throw new RuntimeException("Unexpected response format from Gemini API");
    }

    private List<String> extractStringList(JsonNode node, List<String> fallback) {
        if (node != null && node.isArray()) {
            List<String> list = new ArrayList<>();
            for (JsonNode item : node) {
                list.add(item.asText());
            }
            return list;
        }
        return fallback;
    }

    public ProblemBlueprintDto generateDynamicLocalBlueprint(String rawTitle, String rawDescription) {
        String combined = (rawTitle + " " + rawDescription).toLowerCase();

        // 1. Bakery / Cake Orders
        if (combined.matches(".*(cake|baker|bakery|pastry|cookie|cupcake|confection).*")) {
            return new ProblemBlueprintDto(
                    "SaaS Order Calendar & Custom Cake Recipe Tracker",
                    "Small-batch Bakers & Custom Cake Designers",
                    List.of("Next.js", "Java Spring Boot", "PostgreSQL", "Cloudinary"),
                    List.of(
                            "Visual Order Builder: Form specifying size, shapes, flavors, toppings, and photo references.",
                            "Client Dashboard: Portal for clients to track baking progress (Received, Mixing, Baked, Decorating).",
                            "Interactive Delivery Calendar: Block out date slots once daily capacity is reached.",
                            "Automated Email Alerts: Confirm receipt details to reduce misspellings."
                    ),
                    List.of(
                            "Phase 1: Multi-step order booking funnel with reference uploads.",
                            "Phase 2: Calendar capacity blocking parameters.",
                            "Phase 3: Trigger templates for receipt verification.",
                            "Phase 4: Real-time order state indicators."
                    )
            );
        }

        // 2. Contractor Timesheets & Shift Clock
        if (combined.matches(".*(timesheet|contractor|clock|shift|subcontractor|hour|payroll|construction).*")) {
            return new ProblemBlueprintDto(
                    "Micro-SaaS Billable Hours & Shift Tracker",
                    "Construction Foremen, Site Managers & Subcontractors",
                    List.of("Vite + React", "Java Spring Boot", "PostgreSQL", "Tailwind CSS"),
                    List.of(
                            "One-Tap Clock-in/Clock-out: GPS-stamped start and end times for shift jobs.",
                            "Real-Time Coordinator Board: Dashboard for foreman to view live crew logins.",
                            "Dispute-Free PDF Invoicing: Export timesheets signed digitally at end of shift."
                    ),
                    List.of(
                            "Phase 1: Mobile-friendly shift punch cards.",
                            "Phase 2: Contractor role-based authentication.",
                            "Phase 3: PDF export engine for payroll approval."
                    )
            );
        }

        // 3. Appointments & Reminders
        if (combined.matches(".*(book|appointment|calendar|reminder|patient|clinic|salon|consultation).*")) {
            return new ProblemBlueprintDto(
                    "Automated Pet Care & Appointment Reminders Engine",
                    "Solo Service Providers, Veterinary Clinics & Consultants",
                    List.of("Next.js", "Java Spring Boot", "PostgreSQL", "Twilio API"),
                    List.of(
                            "Two-Way SMS Confirmations: Automated text alerts that update calendar on reply.",
                            "Medication Regimen Portal: Timed reminders sent to patients/clients with instructions.",
                            "No-Show Predictive Scoring: Flags high-risk appointment slots for staff follow-up."
                    ),
                    List.of(
                            "Phase 1: Schedule calendar sync module.",
                            "Phase 2: Twilio Programmable Messaging webhooks.",
                            "Phase 3: Reminder timeline widget."
                    )
            );
        }

        // 4. Default Dynamic Synthesizer
        String cleanTitle = rawTitle.trim();
        String titleWords = cleanTitle.length() > 30 ? cleanTitle.substring(0, 30) + "..." : cleanTitle;
        return new ProblemBlueprintDto(
                titleWords + " Management & Automation Hub",
                "Small Business Operators, Creators & Independent Teams",
                List.of("Java 17", "Spring Boot 3", "PostgreSQL", "React.js"),
                List.of(
                        "Automated Intake Portal: Direct client submission interface for operational bottlenecks.",
                        "Real-Time Activity Dashboard: Track tasks, requests, and pipeline stages in one place.",
                        "Instant Notification Alerts: Automatically ping stakeholders when status changes.",
                        "Export & Reporting Suite: Summary reports for operations and accounting."
                ),
                List.of(
                        "Phase 1: Responsive data submission interface.",
                        "Phase 2: REST API endpoints and data model.",
                        "Phase 3: Real-time status and notification triggers.",
                        "Phase 4: Analytics overview."
                )
        );
    }
}
