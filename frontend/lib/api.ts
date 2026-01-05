import { SmartDisplayData } from './types';

// TODO: Make this configurable
const API_BASE_URL = 'http://localhost:8080/api/v1';

export async function fetchSmartDisplayData(user: string): Promise<SmartDisplayData | null> {
    try {
        const res = await fetch(`${API_BASE_URL}/${user}/smart-display`, {
            next: { revalidate: 60 } // Revalidate every minute
        });

        if (!res.ok) {
            console.error('Failed to fetch data:', res.statusText);
            return null;
        }

        return await res.json();
    } catch (error) {
        console.error('Error fetching smart display data:', error);
        return null;
    }
}
