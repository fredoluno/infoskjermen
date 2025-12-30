package no.infoskjermen;

import no.infoskjermen.data.NetatmoData;
import no.infoskjermen.data.WeatherData;
import no.infoskjermen.data.WeatherDataDay;
import no.infoskjermen.data.CalendarEvent;
import no.infoskjermen.tjenester.NetatmoService;
import no.infoskjermen.tjenester.WeatherService;
import no.infoskjermen.tjenester.CalendarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller for providing JSON data services to React frontend.
 * Handles requests for calendar, weather, and netatmo data based on user
 * identifier (navn).
 * 
 * Base path: /api/v1
 * All endpoints return JSON responses suitable for React consumption.
 */
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final NetatmoService netatmoService;
    private final WeatherService weatherService;
    private final CalendarService calendarService;
    private final no.infoskjermen.tjenester.AiPrioritizationService aiPrioritizationService;

    /**
     * Constructor injection following coding standards.
     * 
     * @param netatmoService          Service for Netatmo sensor data
     * @param weatherService          Service for weather information
     * @param calendarService         Service for calendar events
     * @param aiPrioritizationService Service for AI prioritization
     */
    public ApiController(NetatmoService netatmoService,
            WeatherService weatherService,
            CalendarService calendarService,
            no.infoskjermen.tjenester.AiPrioritizationService aiPrioritizationService) {
        this.netatmoService = netatmoService;
        this.weatherService = weatherService;
        this.calendarService = calendarService;
        this.aiPrioritizationService = aiPrioritizationService;
    }

    /**
     * Get smart display data (Aggregated + AI Prioritized).
     * 
     * @param navn User identifier
     * @return JSON response with prioritized content and raw data
     */
    @GetMapping("/{navn}/smart-display")
    public ResponseEntity<Map<String, Object>> getSmartDisplay(@PathVariable String navn) {
        log.info("Fetching smart display data for user: {}", navn);

        try {
            // 1. Fetch all raw data
            // Note: In production we might want to run these in parallel
            NetatmoData netatmoData = netatmoService.getNetatmoData(navn);
            WeatherData weatherData = weatherService.getWeatherReport(navn);
            var calendarEvents = calendarService.getCalendarEvents(navn);

            // 2. Prepare Context for AI
            Map<String, Object> context = new HashMap<>();
            context.put("netatmo", netatmoData);

            // Simplify weather for AI context to save tokens
            Map<String, Object> weatherContext = new HashMap<>();
            if (weatherData.main != null && weatherData.main.day != null) {
                weatherContext.put("today", weatherData.main.day);
            }
            context.put("weather", weatherContext);

            // Simplify calendar
            context.put("calendar_count", calendarEvents.size());
            if (!calendarEvents.isEmpty()) {
                context.put("next_event", calendarEvents.first().title + " at " + calendarEvents.first().printDato());
            }

            // 3. Call AI
            Map<String, Object> aiPriority = aiPrioritizationService.prioritizeContent(context);

            // 4. Construct Response
            Map<String, Object> response = new HashMap<>();
            response.put("navn", navn);
            response.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));

            response.put("ai_priority", aiPriority);

            // Raw Data Sections
            response.put("netatmo", getNetatmoData(navn).getBody().get("data")); // Reuse existing logic logic
            response.put("weather", getWeatherData(navn).getBody());
            response.put("calendar", getCalendarEvents(navn).getBody());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to fetch smart display data", e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse(navn, "Failed to retrieve smart display data", e.getMessage()));
        }
    }

    /**
     * Get Netatmo sensor data for specified user.
     * 
     * @param navn User identifier
     * @return JSON response with Netatmo sensor data
     */
    @GetMapping("/{navn}/netatmo")
    public ResponseEntity<Map<String, Object>> getNetatmoData(@PathVariable String navn) {
        log.info("Fetching Netatmo data for user: {}", navn);

        try {
            NetatmoData data = netatmoService.getNetatmoData(navn);

            Map<String, Object> response = new HashMap<>();
            response.put("navn", navn);
            response.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));

            Map<String, Object> netatmoData = new HashMap<>();
            netatmoData.put("outdoorTemperature", data.outdoorTemperature);
            netatmoData.put("indoorTemperature", data.indoorTemperature);
            netatmoData.put("outdoorHumidity", data.outdoorHumidity);
            netatmoData.put("indoorHumidity", data.indoorHumidity);
            netatmoData.put("pressure", data.pressure);
            netatmoData.put("co2", data.co2);
            netatmoData.put("noise", data.noise);

            response.put("data", netatmoData);

            log.debug("Successfully retrieved Netatmo data for user: {}", navn);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to fetch Netatmo data for user: {}", navn, e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse(navn, "Failed to retrieve Netatmo data", e.getMessage()));
        }
    }

    /**
     * Get weather data for specified user.
     * 
     * @param navn User identifier
     * @return JSON response with current weather and forecast
     */
    @GetMapping("/{navn}/weather")
    public ResponseEntity<Map<String, Object>> getWeatherData(@PathVariable String navn) {
        log.info("Fetching weather data for user: {}", navn);

        try {
            WeatherData weatherData = weatherService.getWeatherReport(navn);

            Map<String, Object> response = new HashMap<>();
            response.put("navn", navn);
            response.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));

            // Current weather (main)
            Map<String, Object> current = new HashMap<>();
            if (weatherData.main != null) {
                current.put("date", weatherData.main.date != null ? weatherData.main.date.toString() : null);
                current.put("fromToday", weatherData.mainFromToday);

                // Get the most relevant period for current weather
                if (weatherData.main.day != null) {
                    current.put("temperature", weatherData.main.day.temperature);
                    current.put("symbol", weatherData.main.day.symbol);
                    current.put("period", "day");
                } else if (weatherData.main.morning != null) {
                    current.put("temperature", weatherData.main.morning.temperature);
                    current.put("symbol", weatherData.main.morning.symbol);
                    current.put("period", "morning");
                } else if (weatherData.main.evening != null) {
                    current.put("temperature", weatherData.main.evening.temperature);
                    current.put("symbol", weatherData.main.evening.symbol);
                    current.put("period", "evening");
                } else if (weatherData.main.night != null) {
                    current.put("temperature", weatherData.main.night.temperature);
                    current.put("symbol", weatherData.main.night.symbol);
                    current.put("period", "night");
                }
            }
            response.put("current", current);

            // Forecast
            var forecast = weatherData.longtimeForecast.stream()
                    .limit(5) // Limit to 5 days for React frontend
                    .map(this::mapWeatherDayToJson)
                    .toList();

            response.put("forecast", forecast);

            log.debug("Successfully retrieved weather data for user: {}", navn);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to fetch weather data for user: {}", navn, e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse(navn, "Failed to retrieve weather data", e.getMessage()));
        }
    }

    /**
     * Maps WeatherDataDay to JSON structure for React consumption.
     * 
     * @param weatherDay Weather data for a specific day
     * @return Map representing the day's weather data
     */
    private Map<String, Object> mapWeatherDayToJson(WeatherDataDay weatherDay) {
        Map<String, Object> dayData = new HashMap<>();
        dayData.put("date", weatherDay.date != null ? weatherDay.date.toString() : null);

        Map<String, Object> periods = new HashMap<>();
        if (weatherDay.night != null) {
            periods.put("night", mapPeriodToJson(weatherDay.night));
        }
        if (weatherDay.morning != null) {
            periods.put("morning", mapPeriodToJson(weatherDay.morning));
        }
        if (weatherDay.day != null) {
            periods.put("day", mapPeriodToJson(weatherDay.day));
        }
        if (weatherDay.evening != null) {
            periods.put("evening", mapPeriodToJson(weatherDay.evening));
        }

        dayData.put("periods", periods);
        return dayData;
    }

    /**
     * Maps WeatherDataPeriod to JSON structure.
     * 
     * @param period Weather data for a specific time period
     * @return Map representing the period's weather data
     */
    private Map<String, Object> mapPeriodToJson(no.infoskjermen.data.WeatherDataPeriod period) {
        Map<String, Object> periodData = new HashMap<>();
        periodData.put("temperature", period.temperature);
        periodData.put("symbol", period.symbol);
        periodData.put("period", period.period);
        return periodData;
    }

    /**
     * Get calendar events for specified user.
     * 
     * @param navn User identifier
     * @return JSON response with calendar events
     */
    @GetMapping("/{navn}/calendar")
    public ResponseEntity<Map<String, Object>> getCalendarEvents(@PathVariable String navn) {
        log.info("Fetching calendar events for user: {}", navn);

        try {
            var calendarEvents = calendarService.getCalendarEvents(navn);

            Map<String, Object> response = new HashMap<>();
            response.put("navn", navn);
            response.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));

            // Convert calendar events to JSON-friendly format
            var events = calendarEvents.stream()
                    .map(this::mapCalendarEventToJson)
                    .toList();

            response.put("events", events);
            response.put("eventCount", events.size());

            log.debug("Successfully retrieved {} calendar events for user: {}", events.size(), navn);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to fetch calendar events for user: {}", navn, e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse(navn, "Failed to retrieve calendar events", e.getMessage()));
        }
    }

    /**
     * Maps CalendarEvent to JSON structure for React consumption.
     * 
     * @param event Calendar event to map
     * @return Map representing the event data
     */
    private Map<String, Object> mapCalendarEventToJson(CalendarEvent event) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", event.title);
        eventData.put("start", event.eventStart != null ? event.eventStart.format(ISO_FORMATTER) : null);
        eventData.put("end", event.eventEnd != null ? event.eventEnd.format(ISO_FORMATTER) : null);

        // Include the formatted Norwegian date display for backwards compatibility
        eventData.put("displayDate", event.printDato());
        eventData.put("displayText", event.print());

        // Add some helper flags for React frontend
        eventData.put("allDay", event.eventStart != null && event.eventEnd != null &&
                event.eventStart.toLocalTime().equals(java.time.LocalTime.MIDNIGHT) &&
                event.eventEnd.toLocalTime().equals(java.time.LocalTime.MIDNIGHT));

        return eventData;
    }

    /**
     * Creates a standardized error response structure.
     * 
     * @param navn    User identifier
     * @param message Error message
     * @param details Detailed error information
     * @return Error response map
     */
    private Map<String, Object> createErrorResponse(String navn, String message, String details) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("navn", navn);
        errorResponse.put("timestamp", LocalDateTime.now().format(ISO_FORMATTER));
        errorResponse.put("error", true);
        errorResponse.put("message", message);
        errorResponse.put("details", details);
        return errorResponse;
    }
}