# REST API Development TODO

## Overview
Create a new REST API to support React frontend with JSON data responses for calendar, netatmo, and weather services based on the `navn` parameter.

## Architecture Decision
- **New Controller Class**: Create `ApiController.java` separate from `InfoskjermenApplication.java`
- **Reason**: Follow separation of concerns and coding standards
- **Future Security**: Easier to add security annotations to dedicated API controller

## Implementation Plan

### Phase 1: Foundation and Netatmo API
- [ ] **Create ApiController class**
  - Follow coding standards (constructor injection, proper logging)
  - Location: `src/main/java/no/infoskjermen/ApiController.java`
  - Base path: `/api/v1`

- [ ] **Implement Netatmo API endpoint**
  - Endpoint: `GET /api/v1/{navn}/netatmo`
  - Response: JSON format suitable for React consumption
  - Include error handling and logging
  - Test with existing netatmo service

### Phase 2: Weather API
- [ ] **Implement Weather API endpoint**
  - Endpoint: `GET /api/v1/{navn}/weather`
  - Response: Structured JSON with current weather and forecast
  - Handle both main weather and long-time forecast data

### Phase 3: Calendar API  
- [ ] **Implement Calendar API endpoint**
  - Endpoint: `GET /api/v1/{navn}/calendar`
  - Response: JSON array of calendar events
  - Include event details suitable for React display

### Phase 4: Enhancement
- [ ] **Add comprehensive error handling**
  - Consistent JSON error responses
  - Proper HTTP status codes
  - Detailed logging following coding standards

- [ ] **Add API documentation**
  - JavaDoc comments
  - Consider OpenAPI/Swagger annotations

- [ ] **Prepare security structure**
  - Plan authentication/authorization approach
  - Consider rate limiting
  - API key or JWT token structure

### Phase 5: Integration and Testing
- [ ] **Create integration tests**
  - Follow testing standards from CODING-STANDARDS.md
  - Test JSON response structures
  - Test error scenarios

- [ ] **Update existing endpoints (optional)**
  - Consider refactoring current HTML endpoints
  - Maintain backward compatibility

## JSON Response Structure Examples

### Netatmo Response
```json
{
  "navn": "fredrik",
  "timestamp": "2025-12-28T10:30:00Z",
  "data": {
    "outdoorTemperature": 12.5,
    "humidity": 65,
    "pressure": 1013.2
  }
}
```

### Weather Response  
```json
{
  "navn": "fredrik", 
  "timestamp": "2025-12-28T10:30:00Z",
  "current": {
    "temperature": 15.2,
    "description": "Partly cloudy",
    "windSpeed": 5.1
  },
  "forecast": [
    {
      "date": "2025-12-29",
      "high": 18,
      "low": 8,
      "description": "Sunny"
    }
  ]
}
```

### Calendar Response
```json
{
  "navn": "fredrik",
  "timestamp": "2025-12-28T10:30:00Z", 
  "events": [
    {
      "title": "Meeting",
      "start": "2025-12-28T14:00:00Z",
      "end": "2025-12-28T15:00:00Z",
      "allDay": false
    }
  ]
}
```

## Technical Notes
- Use constructor injection following coding standards
- Proper SLF4J logging with class-specific logger
- Follow exception handling patterns from standards
- Consider caching for expensive API calls
- Ensure thread safety for concurrent requests

## Security Considerations (Future)
- API authentication (JWT tokens or API keys)
- Rate limiting per user/navn
- Input validation and sanitization
- CORS configuration for React frontend
- Request logging for security auditing