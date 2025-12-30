"use client";

import React, { useEffect, useState } from 'react';
import { SmartDisplayData } from '@/lib/types';
import { Cloud, Sun, Wind, Calendar, Info, Trash2 } from 'lucide-react';

interface Props {
    data: SmartDisplayData;
    mode: string;
}

export default function SmartDisplay({ data, mode }: Props) {
    const [time, setTime] = useState(new Date());
    const isGrayscale = mode === 'grayscale';

    useEffect(() => {
        const timer = setInterval(() => setTime(new Date()), 1000);
        return () => clearInterval(timer);
    }, []);

    const formatTime = (date: Date) => {
        return date.toLocaleTimeString('nb-NO', { hour: '2-digit', minute: '2-digit' });
    };

    const formatDate = (date: Date) => {
        return date.toLocaleDateString('nb-NO', { weekday: 'long', day: 'numeric', month: 'long' });
    };

    if (isGrayscale) {
        return (
            <div className="e-ink-mode min-h-screen p-4 flex flex-col font-mono uppercase">
                <header className="border-b-4 border-black pb-2 mb-4 flex justify-between items-end">
                    <div>
                        <h1 className="text-4xl font-bold">{formatDate(time)}</h1>
                        <p className="text-xl mt-1">{data.ai_priority.summary}</p>
                    </div>
                    <div className="text-6xl font-bold">{formatTime(time)}</div>
                </header>

                <main className="flex-1 grid grid-cols-1 gap-8">
                    <section className="border-2 border-black p-4">
                        <h2 className="text-2xl font-bold border-b-2 border-black mb-2 flex items-center gap-2">
                            <Calendar className="w-6 h-6" /> KALENDER
                        </h2>
                        {data.calendar.events && data.calendar.events.slice(0, 3).map((event, i) => (
                            <div key={i} className="mb-2">
                                <span className="font-bold">{event.displayDate}</span>: {event.title}
                            </div>
                        ))}
                    </section>

                    <section className="border-2 border-black p-4">
                        <h2 className="text-2xl font-bold border-b-2 border-black mb-2 flex items-center gap-2">
                            <Sun className="w-6 h-6" /> VÆRET
                        </h2>
                        <div className="text-3xl">
                            {data.weather.current?.temperature}°C {data.weather.current?.symbol}
                        </div>
                    </section>
                </main>
            </div>
        );
    }

    // COLOR MODE (Landscape)
    return (
        <div className="min-h-screen bg-slate-900 text-white p-6 relative overflow-hidden font-sans">
            {/* Background Gradient based on focus? */}
            <div className="absolute inset-0 bg-gradient-to-br from-blue-900 to-slate-900 opacity-50 z-0"></div>

            <div className="relative z-10 grid grid-cols-3 gap-6 h-full">
                {/* Left Column: Time & Status */}
                <div className="col-span-1 flex flex-col gap-6">
                    <div className="bg-white/10 backdrop-blur-md rounded-3xl p-6 border border-white/10 shadow-xl">
                        <h1 className="text-2xl text-slate-300 capitalize">{formatDate(time)}</h1>
                        <div className="text-8xl font-bold tracking-tighter mt-2">{formatTime(time)}</div>
                    </div>

                    <div className="bg-white/10 backdrop-blur-md rounded-3xl p-6 border border-white/10 shadow-xl flex-1">
                        <h2 className="text-sm uppercase tracking-widest text-slate-400 mb-4">AI Insight</h2>
                        <div className="text-2xl font-medium leading-relaxed">
                            "{data.ai_priority.summary}"
                        </div>
                        <div className="mt-4 flex gap-2">
                            <span className="px-3 py-1 bg-blue-500/20 text-blue-300 rounded-full text-sm">
                                Focus: {data.ai_priority.focus}
                            </span>
                        </div>
                    </div>
                </div>

                {/* Middle Column: Main Content (Dynamic) */}
                <div className="col-span-1 flex flex-col gap-6">
                    <div className="bg-white/10 backdrop-blur-md rounded-3xl p-6 border border-white/10 shadow-xl h-full">
                        <h2 className="flex items-center gap-2 text-xl font-bold mb-6 text-slate-200">
                            <Calendar className="w-6 h-6 text-blue-400" /> Kommende
                        </h2>
                        <div className="space-y-6">
                            {data.calendar.events && data.calendar.events.slice(0, 4).map((event, i) => (
                                <div key={i} className="flex gap-4 items-start">
                                    <div className="w-12 text-center pt-1">
                                        <span className="block text-sm font-bold text-slate-400">{event.displayDate}</span>
                                    </div>
                                    <div>
                                        <h3 className="text-lg font-medium text-white">{event.title}</h3>
                                        <p className="text-sm text-slate-400">{event.start ? event.start.split('T')[1].substring(0, 5) : 'All day'}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Right Column: Outdoor Environment & Weather */}
                <div className="col-span-1 flex flex-col gap-6">

                    {/* Module 1: Utemiljø (Actual Sensors) */}
                    <div className="bg-gradient-to-br from-emerald-500/20 to-teal-500/20 backdrop-blur-md rounded-3xl p-6 border border-white/10 shadow-xl">
                        <h2 className="flex items-center gap-2 text-xl font-bold mb-4 text-slate-200">
                            <Wind className="w-6 h-6 text-emerald-300" /> Utemiljø
                        </h2>

                        {/* Main Sensor: Temperature */}
                        <div className="flex items-center justify-between mb-4">
                            <div className="flex flex-col">
                                <span className="text-sm text-slate-400 uppercase tracking-widest">Temperatur</span>
                                <span className="text-6xl font-bold">{data.netatmo.outdoorTemperature}°</span>
                            </div>
                            {/* Rain (if available) */}
                            {data.netatmo.rain !== undefined && (
                                <div className="flex flex-col items-end">
                                    <span className="text-sm text-slate-400 uppercase tracking-widest">Regn</span>
                                    <span className="text-4xl font-bold text-blue-300">{data.netatmo.rain} mm</span>
                                </div>
                            )}
                        </div>

                        {/* Secondary Sensors: Wind & Humidity */}
                        <div className="grid grid-cols-2 gap-4">
                            {data.netatmo.windStrength !== undefined && (
                                <div className="p-3 bg-white/5 rounded-xl">
                                    <span className="block text-2xl font-bold">{data.netatmo.windStrength} <span className="text-sm font-normal text-slate-400">km/h</span></span>
                                    <span className="text-xs text-slate-500">Vind ({data.netatmo.windAngle}°)</span>
                                </div>
                            )}
                            <div className="p-3 bg-white/5 rounded-xl">
                                <span className="block text-2xl font-bold">{data.netatmo.outdoorHumidity}%</span>
                                <span className="text-xs text-slate-500">Fuktighet</span>
                            </div>
                        </div>
                    </div>

                    {/* Module 2: Weather Forecast (Yr) */}
                    <div className="bg-gradient-to-br from-blue-500/20 to-purple-500/20 backdrop-blur-md rounded-3xl p-6 border border-white/10 shadow-xl flex-1">
                        <h2 className="flex items-center gap-2 text-xl font-bold mb-4 text-slate-200">
                            <Cloud className="w-6 h-6 text-blue-300" /> Værmelding
                        </h2>

                        {/* Today's Forecast (Parts) */}
                        {data.weather.today && data.weather.today.periods && (
                            <div className="mb-6">
                                <h3 className="text-sm text-slate-400 uppercase mb-2">I dag</h3>
                                <div className="grid grid-cols-4 gap-2 text-center">
                                    {data.weather.today.periods.morning && (
                                        <div className="bg-white/5 rounded-lg p-2">
                                            <div className="text-xs text-slate-400">Morgen</div>
                                            <div className="font-bold">{data.weather.today.periods.morning.temperature}°</div>
                                        </div>
                                    )}
                                    {data.weather.today.periods.day && (
                                        <div className="bg-white/5 rounded-lg p-2">
                                            <div className="text-xs text-slate-400">Dag</div>
                                            <div className="font-bold">{data.weather.today.periods.day.temperature}°</div>
                                        </div>
                                    )}
                                    {data.weather.today.periods.evening && (
                                        <div className="bg-white/5 rounded-lg p-2">
                                            <div className="text-xs text-slate-400">Kveld</div>
                                            <div className="font-bold">{data.weather.today.periods.evening.temperature}°</div>
                                        </div>
                                    )}
                                    {data.weather.today.periods.night && (
                                        <div className="bg-white/5 rounded-lg p-2">
                                            <div className="text-xs text-slate-400">Natt</div>
                                            <div className="font-bold">{data.weather.today.periods.night.temperature}°</div>
                                        </div>
                                    )}
                                </div>
                            </div>
                        )}

                        {/* Tomorrow's Forecast */}
                        {data.weather.forecast && data.weather.forecast.length > 0 && (
                            <div>
                                <h3 className="text-sm text-slate-400 uppercase mb-2">I morgen ({data.weather.forecast[0].date})</h3>
                                <div className="flex items-center justify-between bg-white/5 rounded-xl p-3">
                                    <div className="flex gap-4">
                                        {data.weather.forecast[0].periods.day ? (
                                            <div className="text-2xl font-bold">{data.weather.forecast[0].periods.day.temperature}° <span className="text-sm font-normal text-slate-400">{data.weather.forecast[0].periods.day.symbol}</span></div>
                                        ) : (
                                            <span className="text-slate-500">Ingen data</span>
                                        )}
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
