package no.infoskjermen.tjenester;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException; // Kept to avoid unused import error if I remove it, actually I should just remove it but I will keep it clean.
// Actually, let's remove unused imports.

/**
 * Service for interacting with Google Vertex AI to prioritize content.
 */
@Service
public class AiPrioritizationService {

    private static final Logger log = LoggerFactory.getLogger(AiPrioritizationService.class);
    private static final String MODEL_NAME = "gemini-pro";

    @Value("${google.cloud.project-id:infoskjermen}")
    private String projectId;

    @Value("${google.cloud.location:europe-west1}")
    private String location;

    /**
     * Analyzes the provided context and returns a prioritization summary.
     *
     * @param contextData Map containing weather, calendar, and sensor data.
     * @return Map with prioritization key/values.
     */
    public java.util.Map<String, Object> prioritizeContent(java.util.Map<String, Object> contextData) {
        log.info("Prioritizing content using Vertex AI ({})", location);
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        try (VertexAI vertexAi = new VertexAI(projectId, location)) {
            GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAi);

            // Convert context to JSON for the prompt
            String contextJson = mapper.writeValueAsString(contextData);

            String prompt = String.format(
                    """
                            You are an intelligent display assistant. Analyze the following data (Weather, Calendar, Sensors) and decide what is most important to show on a home info screen.

                            Data:
                            %s

                            Return ONLY a valid JSON object with the following structure:
                            {
                                "focus": "weather" | "calendar" | "transport" | "garbage" | "normal",
                                "reason": "Short explanation",
                                "summary": "One line summary to display",
                                "urgent": false
                            }
                            """,
                    contextJson);

            GenerateContentResponse response = model.generateContent(prompt);
            String output = ResponseHandler.getText(response);

            // Cleanup markdown code blocks if present
            output = output.replaceAll("```json", "").replaceAll("```", "").trim();

            log.debug("AI Response: {}", output);

            // Parse response to Map
            return mapper.readValue(output,
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {
                    });

        } catch (Exception e) {
            log.error("Failed to call Vertex AI", e);
            // Fallback response
            java.util.Map<String, Object> fallback = new java.util.HashMap<>();
            fallback.put("focus", "normal");
            fallback.put("reason", "AI unavailable: " + e.getMessage());
            fallback.put("summary", "System operational (AI Offline)");
            fallback.put("urgent", false);
            return fallback;
        }
    }
}
