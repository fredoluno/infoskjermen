export interface SmartDisplayData {
  navn: string;
  timestamp: string;
  ai_priority: AiPriority;
  netatmo: NetatmoData;
  weather: WeatherData;
  calendar: {
    events: CalendarData[];
    eventCount: number;
    navn?: string;
    timestamp?: string;
  };
  calendar_count?: number; // Backend might send this separately too
}

export interface AiPriority {
  focus: 'weather' | 'calendar' | 'transport' | 'garbage' | 'normal';
  reason: string;
  summary: string;
  urgent: boolean;
}

export interface NetatmoData {
  outdoorTemperature?: string;
  indoorTemperature?: string;
  outdoorHumidity?: string;
  indoorHumidity?: string;
  pressure?: string;
  co2?: string;
  noise?: string;
}

export interface WeatherData {
  // Add specific fields based on usage
  current?: {
    temperature?: string;
    symbol?: string;
    period?: string;
  };
  forecast?: WeatherForecast[];
}

export interface WeatherForecast {
  date: string;
  periods: {
    morning?: WeatherPeriod;
    day?: WeatherPeriod;
    evening?: WeatherPeriod;
    night?: WeatherPeriod;
  };
}

export interface WeatherPeriod {
  temperature: string;
  symbol: string;
  period: string;
}

export interface CalendarData {
  title: string;
  start: string;
  end: string;
  displayDate: string;
  displayText: string;
  allDay: boolean;
}
