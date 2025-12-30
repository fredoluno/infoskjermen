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

                {/* Right Column: Weather & Sensors */}
                <div className="col-span-1 flex flex-col gap-6">
                    <div className="bg-gradient-to-br from-blue-500/20 to-purple-500/20 backdrop-blur-md rounded-3xl p-6 border border-white/10 shadow-xl">
                        <h2 className="flex items-center gap-2 text-xl font-bold mb-4 text-slate-200">
                            <Cloud className="w-6 h-6 text-blue-300" /> Været nå
                        </h2>
                        <div className="flex items-center justify-between">
                            <span className="text-6xl font-bold">{data.weather.current?.temperature}°</span>
                            <span className="text-xl text-slate-300">{data.weather.current?.symbol}</span>
                        </div>
                        {data.netatmo.outdoorHumidity && (
                            <div className="mt-4 flex gap-4 text-sm text-slate-400">
                                <span>💧 {data.netatmo.outdoorHumidity}%</span>
                                <span>💨 {data.netatmo.outdoorTemperature}°C (Netatmo)</span>
                            </div>
                        )}
                    </div>

                    <div className="bg-white/5 backdrop-blur-md rounded-3xl p-6 border border-white/10 shadow-xl flex-1">
                        <h2 className="text-sm uppercase tracking-widest text-slate-400 mb-4">Inneklima</h2>
                        <div className="grid grid-cols-2 gap-4">
                            <div className="p-3 bg-white/5 rounded-xl">
                                <span className="block text-2xl font-bold">{data.netatmo.indoorTemperature}°</span>
                                <span className="text-xs text-slate-500">Stue</span>
                            </div>
                            <div className="p-3 bg-white/5 rounded-xl">
                                <span className="block text-2xl font-bold">{data.netatmo.indoorHumidity}%</span>
                                <span className="text-xs text-slate-500">Fuktighet</span>
                            </div>
                            <div className="p-3 bg-white/5 rounded-xl">
                                <span className="block text-2xl font-bold">{data.netatmo.co2}</span>
                                <span className="text-xs text-slate-500">CO2</span>
                            </div>
                            <div className="p-3 bg-white/5 rounded-xl">
                                <span className="block text-2xl font-bold">{data.netatmo.noise}</span>
                                <span className="text-xs text-slate-500">dB</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
